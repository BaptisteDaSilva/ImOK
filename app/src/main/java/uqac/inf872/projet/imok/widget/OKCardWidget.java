package uqac.inf872.projet.imok.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.api.RecipientListHelper;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.Utils;

import static android.os.SystemClock.sleep;

public class OKCardWidget extends AppWidgetProvider {

    private static final String PERMS_SEND_SMS = Manifest.permission.SEND_SMS;

    // Intitulé de l'extra qui contient la direction du défilé
    private final static String EXTRA_DIRECTION = "extraDirection";

    // La valeur pour défiler vers la gauche
    private final static String EXTRA_PREVIOUS = "previous";

    // La valeur pour défiler vers la droite
    private final static String EXTRA_NEXT = "next";

    // Intitulé de l'extra qui contient l'indice actuel dans le tableau des tutos
    private final static String EXTRA_INDICE = "extraIndice";

    // Action qui indique qu'on essaie d'ouvrir ou de créer une OKCard
    private final static String ACTION_CREATE_OKCARD = "uqac.inf872.projet.imok.receiver.widget.action.ACTION_CREATE_OKCARD";
    private final static String ACTION_OPEN_OKCARD = "uqac.inf872.projet.imok.receiver.widget.action.ACTION_OPEN_OKCARD";
    private final static String ACTION_SEND = "uqac.inf872.projet.imok.receiver.widget.action.ACTION_SEND";
    private final static String ACTION_REFRESH = "uqac.inf872.projet.imok.receiver.widget.action.ACTION_REFRESH";
    static Task taskRecipientList;
    static Task taskPosition;
    static Task taskPosition2;
    private static ArrayList<String> idOKCard = new ArrayList<>();
    private static OKCard okCard;
    private static RecipientList recipientList;
    private static ArrayList<Position> positions = new ArrayList<>();
    private static ListenerRegistration registrationOKCard;
    private static ListenerRegistration registrationRecipientList;
    private static ListenerRegistration registrationPosition;
    private static Executor setDataExecutor = Executors.newSingleThreadExecutor();
    private static Executor initExecutor = Executors.newSingleThreadExecutor();

    // Indice actuel dans le tableau
    private static int indice = 0;

    private static boolean initOK;

    private static void setData(Context context) {
        taskRecipientList = null;
        taskPosition = null;
        taskPosition2 = null;

        OKCardHelper.getOKCard(idOKCard.get(indice)).get().addOnSuccessListener(setDataExecutor, documentSnapshot ->
        {
            okCard = documentSnapshot.toObject(OKCard.class);

            taskRecipientList = RecipientListHelper.getRecipientList(okCard.getIdListe())
                    .addOnSuccessListener(setDataExecutor, documentSnapshotRecipient ->
                            recipientList = documentSnapshotRecipient.toObject(RecipientList.class));

            positions.clear();

            String idPosition;

            switch (okCard.getIdTrigger().size()) {
                case 2:
                    idPosition = okCard.getIdTrigger().get(1);

                    taskPosition2 = PositionHelper.getPosition(idPosition)
                            .addOnSuccessListener(setDataExecutor, documentSnapshotRecipient ->
                                    positions.add(documentSnapshotRecipient.toObject(Position.class)));
                case 1:
                    idPosition = okCard.getIdTrigger().get(0);

                    taskPosition = PositionHelper.getPosition(idPosition)
                            .addOnSuccessListener(setDataExecutor, documentSnapshotRecipient ->
                                    positions.add(documentSnapshotRecipient.toObject(Position.class)));
            }
        }).addOnFailureListener(e -> Utils.onFailureListener(context, e));

        while (taskRecipientList == null || !taskRecipientList.isComplete()) {
            sleep(100);
        }

        switch (okCard.getIdTrigger().size()) {
            case 2:
                while (taskPosition2 == null || !taskPosition2.isComplete()) {
                    sleep(100);
                }
            case 1:
                while (taskPosition == null || !taskPosition.isComplete()) {
                    sleep(100);
                }
                break;
        }
    }

    private static void initData(Context context) {
        okCard = null;
        recipientList = null;
        positions = new ArrayList<>();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(), OKCardWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        Query query = OKCardHelper.getOKCard();

        if ( query != null ) {


            query.get().addOnSuccessListener(querySnapshot ->
            {
                if ( !querySnapshot.isEmpty() ) {
                    setData(context);

                    context.sendBroadcast(createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_NEXT));
                }

                initOK = true;
            });

            registrationOKCard = query.addSnapshotListener(initExecutor, (value, e) -> {
                if ( e != null ) {
                    Utils.onFailureListener(context, e);
                    return;
                }

                if ( value != null ) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                idOKCard.add(dc.getDocument().getId());

                                if ( idOKCard.size() > 1 ) {
                                    views.setViewVisibility(R.id.widget_btn_left, View.VISIBLE);
                                    views.setViewVisibility(R.id.widget_btn_right, View.VISIBLE);

                                    appWidgetManager.updateAppWidget(componentName, views);
                                }

                                break;
                            case MODIFIED:
                                if ( okCard != null && dc.getDocument().getId().equals(okCard.getId()) ) {
                                    okCard = dc.getDocument().toObject(OKCard.class);

                                    setData(context);

                                    setOKCard(views);
                                    setRecipientList(views);
                                    setPositions(views);

                                    appWidgetManager.updateAppWidget(componentName, views);
                                }

                                break;
                            case REMOVED:
                                idOKCard.remove(dc.getDocument().getId());

                                if ( okCard != null && dc.getDocument().getId().equals(okCard.getId()) ) {
                                    context.sendBroadcast(createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_NEXT));
                                }

                                break;
                        }
                    }
                }
            });

            registrationRecipientList = RecipientListHelper.getRecipientList().addSnapshotListener(initExecutor, (value, e) -> {
                if ( e != null ) {
                    Utils.onFailureListener(context, e);
                    return;
                }

                if ( value != null ) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if ( DocumentChange.Type.MODIFIED == dc.getType() && recipientList != null && dc.getDocument().getId().equals(recipientList.getId()) ) {

                            recipientList = dc.getDocument().toObject(RecipientList.class);

                            setRecipientList(views);

                            appWidgetManager.updateAppWidget(componentName, views);
                        }
                    }
                }
            });

            registrationPosition = PositionHelper.getPosition().addSnapshotListener(initExecutor, (value, e) -> {
                if ( e != null ) {
                    Utils.onFailureListener(context, e);
                    return;
                }

                if ( value != null ) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if ( DocumentChange.Type.MODIFIED == dc.getType() && !positions.isEmpty() ) {

                            ArrayList<Position> newPosition = new ArrayList<>();

                            for (Position oldPosition : positions) {
                                if ( oldPosition.getId().equals(dc.getDocument().getId()) ) {
                                    newPosition.add(dc.getDocument().toObject(Position.class));
                                } else {
                                    newPosition.add(oldPosition);
                                }
                            }

                            positions = newPosition;

                            setPositions(views);

                            appWidgetManager.updateAppWidget(componentName, views);
                        }
                    }
                }
            });
        }
    }

    private static Intent createIntentNextOrPreviousOkCard(Context context, int[] appWidgetIds, String extraDirection) {
        Intent nextIntent = new Intent(context, OKCardWidget.class);

        nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        nextIntent.putExtra(EXTRA_DIRECTION, extraDirection);
        nextIntent.putExtra(EXTRA_INDICE, indice);

        Uri data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_btn_left));
        nextIntent.setData(data);

        return nextIntent;
    }

    private static void setOKCard(RemoteViews views) {

        // On met les bon texte
        views.setTextViewText(R.id.widget_ok_card_name, okCard.getName());
//                views.setImageViewUri(R.id.widget_ok_card_image, okCard.getUrlPicture()); // TODO gérer
        views.setTextViewText(R.id.widget_ok_card_message, okCard.getMessage());
    }

    private static void setRecipientList(RemoteViews views) {
        StringBuilder sb = new StringBuilder();

        for (String s : recipientList.getRecipients()) {
            sb.append(s).append("\n");
        }

        views.setTextViewText(R.id.widget_ok_card_recipient_list_name, recipientList.getName());
        views.setTextViewText(R.id.widget_ok_card_recipient_list, sb.toString());
    }

    private static void setPositions(RemoteViews views) {
        views.setViewVisibility(R.id.widget_ok_card_wifi, View.GONE);
        views.setViewVisibility(R.id.widget_ok_card_gps, View.GONE);

        for (Position position : positions) {
            if ( position.isWifi() ) {
                views.setViewVisibility(R.id.widget_ok_card_wifi, View.VISIBLE);
                views.setTextViewText(R.id.widget_ok_card_wifi_name, position.getName());
            } else {
                views.setViewVisibility(R.id.widget_ok_card_gps, View.VISIBLE);
                views.setTextViewText(R.id.widget_ok_card_gps_name, position.getName());
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // On récupère le RemoteViews qui correspond à l'AppWidget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            if ( !initOK ) {
                views.setViewVisibility(R.id.widget_ok_card, View.GONE);
                views.setViewVisibility(R.id.widget_ok_card_empty, View.GONE);
                views.setViewVisibility(R.id.widget_ok_card_sync, View.VISIBLE);
            } else {
                if ( idOKCard.isEmpty() || okCard == null ) {
                    views.setViewVisibility(R.id.widget_ok_card, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_card_sync, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_card_empty, View.VISIBLE);

                    // La section suivante est destinée à l'ouverture d'une OKCard
                    // L'intent ouvre cette classe même…
                    Intent createIntent = new Intent(context, OKCardWidget.class);

                    // Action l'action ACTION_OPEN_OKCARD
                    createIntent.setAction(ACTION_CREATE_OKCARD);

                    // On ajoute l'intent dans un PendingIntent
                    PendingIntent createPending = PendingIntent.getBroadcast(context, 2, createIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_ok_card_create, createPending);
                } else {
                    views.setViewVisibility(R.id.widget_ok_card_empty, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_card_sync, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_card, View.VISIBLE);

                    if ( idOKCard.size() > 1 ) {
                        views.setViewVisibility(R.id.widget_btn_left, View.VISIBLE);
                        views.setViewVisibility(R.id.widget_btn_right, View.VISIBLE);
                    } else {
                        views.setViewVisibility(R.id.widget_btn_left, View.GONE);
                        views.setViewVisibility(R.id.widget_btn_right, View.GONE);
                    }

                    setOKCard(views);
                    setRecipientList(views);
                    setPositions(views);

                    // La section suivante est destinée à l'ouverture d'une OKCard
                    // L'intent ouvre cette classe même…
                    Intent openIntent = new Intent(context, OKCardWidget.class);

                    // Action l'action ACTION_OPEN_OKCARD
                    openIntent.setAction(ACTION_OPEN_OKCARD);
                    // Et l'id de l'OKCard a ouvrir
                    openIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
                    openIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL, okCard.getUrlPicture());

                    // On ajoute l'intent dans un PendingIntent
                    PendingIntent openPending = PendingIntent.getBroadcast(context, 2, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_ok_card_name, openPending);
                    views.setOnClickPendingIntent(R.id.widget_ok_card_image, openPending);

                    // La section suivante est destinée à l'envoie d'un message d'une OKCard
                    // L'intent ouvre cette classe même…
                    Intent sendIntent = new Intent(context, OKCardWidget.class);

                    // Action l'action ACTION_OPEN_OKCARD
                    sendIntent.setAction(ACTION_SEND);
                    // Et l'id de l'OKCard a ouvrir
                    sendIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());

                    // On ajoute l'intent dans un PendingIntent
                    PendingIntent sendPending = PendingIntent.getBroadcast(context, 2, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_ok_card_send, sendPending);
                }
            }

            //*******************NEXT*********************************
            // On insère l'intent dans un PendingIntent
            PendingIntent nextPending = PendingIntent.getBroadcast(context, 0,
                    createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_NEXT),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Et on l'associe à l'activation du bouton
            views.setOnClickPendingIntent(R.id.widget_btn_right, nextPending);

            //*******************PREVIOUS*****************************
            PendingIntent previousPending = PendingIntent.getBroadcast(context, 1,
                    createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_PREVIOUS),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_btn_left, previousPending);

            // Et il faut mettre à jour toutes les vues
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        if ( registrationOKCard != null ) {
            // Stop listening to changes
            registrationOKCard.remove();
        }

        if ( registrationRecipientList != null ) {
            // Stop listening to changes
            registrationRecipientList.remove();
        }

        if ( registrationPosition != null ) {
            // Stop listening to changes
            registrationPosition.remove();
        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Si l'action est celle d'ouverture de carte
        if ( ACTION_CREATE_OKCARD.equals(intent.getAction()) ) {

            Intent intentOKCardActivity = new Intent(context.getApplicationContext(), OKCardActivity.class);
            context.startActivity(intentOKCardActivity);

        } else if ( ACTION_OPEN_OKCARD.equals(intent.getAction()) ) {

            if ( intent.getExtras() != null ) {
                Intent intentOKCardActivity = new Intent(context.getApplicationContext(), OKCardActivity.class);
                intentOKCardActivity.putExtras(intent.getExtras());
                context.startActivity(intentOKCardActivity);
            }

        } else if ( ACTION_SEND.equals(intent.getAction()) ) {

            sendMessage(context, intent.getExtras().getString(OKCardActivity.BUNDLE_KEY_OK_CARD_ID));

        } else if ( ACTION_REFRESH.equals(intent.getAction()) ) {

            Utils.openMenu(context, Utils.Menu.OKCard);

        } else if ( AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction()) ) {
            if ( idOKCard.isEmpty() ) {
                initData(context);
            }

            // Sinon, s'il s'agit d'une demande de mise à jour
            // On récupère l'indice passé en extra, ou -1 s'il n'y a pas d'indice
            int tmp = intent.getIntExtra(EXTRA_INDICE, -1);

            // S'il y avait bien un indice passé
            if ( tmp != -1 && !idOKCard.isEmpty() ) {
                // On récupère la direction
                String extra = intent.getStringExtra(EXTRA_DIRECTION);
                // Et on calcule l'indice voulu par l'utilisateur
                if ( extra.equals(EXTRA_PREVIOUS) ) {
                    indice = (tmp - 1) % idOKCard.size();
                    if ( indice < 0 ) {
                        indice += idOKCard.size();
                    }
                } else {
                    if ( extra.equals(EXTRA_NEXT) ) {
                        indice = (tmp + 1) % idOKCard.size();
                    }
                }

                setData(context);
            }
        }

        // On revient au traitement naturel du Receiver, qui va lancer onUpdate s'il y a demande de mise à jour
        super.onReceive(context, intent);
    }

    private void sendMessage(Context context, String idOKCard) {
        if ( EasyPermissions.hasPermissions(context, PERMS_SEND_SMS) ) {
            OKCardHelper.getOKCard(idOKCard).get()
                    .addOnSuccessListener(documentSnapshotOKCard ->
                    {
                        OKCard currentOKCard = documentSnapshotOKCard.toObject(OKCard.class);

                        RecipientListHelper.getRecipientList(currentOKCard.getIdListe())
                                .addOnSuccessListener(documentSnapshotRecipient ->
                                {
                                    RecipientList recipientList = documentSnapshotRecipient.toObject(RecipientList.class);

                                    Utils.sendMessage(recipientList.getRecipients(), currentOKCard.getMessage());
                                });
                    });

            Toast.makeText(context, "Message envoyé", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "PERMS_SEND_SMS", Toast.LENGTH_LONG).show();
        }
    }
}
