package uqac.inf872.projet.imok.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
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
import uqac.inf872.projet.imok.controllers.activities.MainActivity;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.Utils;

import static android.os.SystemClock.sleep;

public class OKCardsWidget extends AppWidgetProvider {

    // Intitulé de l'extra qui contient la direction du défilé
    private final static String EXTRA_DIRECTION = "extraDirection";

    // La valeur pour défiler vers la gauche
    private final static String EXTRA_PREVIOUS = "previous";

    // La valeur pour défiler vers la droite
    private final static String EXTRA_NEXT = "next";

    // Intitulé de l'extra qui contient l'indice actuel dans le tableau des tutos
    private final static String EXTRA_INDICE = "extraIndice";

    // Action qui indique ce qu'on essaie de faire
    private final static String ACTION_CREATE_OKCARD = "uqac.inf872.projet.imok.receiver.widget_ok_cards.action.ACTION_CREATE_OKCARD";
    private final static String ACTION_OPEN_OKCARD = "uqac.inf872.projet.imok.receiver.widget_ok_cards.action.ACTION_OPEN_OKCARD";
    private final static String ACTION_SEND = "uqac.inf872.projet.imok.receiver.widget_ok_cards.action.ACTION_SEND";
    private final static String ACTION_CONNECTION = "uqac.inf872.projet.imok.receiver.widget_ok_cards.action.ACTION_CONNECTION";

    // Task
    static Task taskRecipientList;
    static Task taskPosition;
    static Task taskPosition2;

    // Liste d'identifiant des OKCard
    private static ArrayList<String> idOKCard = new ArrayList<>();

    // Valeur a affiché
    private static OKCard okCard;
    private static RecipientList recipientList;
    private static ArrayList<Position> positions = new ArrayList<>();

    // Listener
    private static ListenerRegistration registrationOKCard;
    private static ListenerRegistration registrationRecipientList;
    private static ListenerRegistration registrationPosition;

    // Executor
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
            sleep(50);
        }

        switch (okCard.getIdTrigger().size()) {
            case 2:
                while (taskPosition2 == null || !taskPosition2.isComplete()) {
                    sleep(50);
                }
            case 1:
                while (taskPosition == null || !taskPosition.isComplete()) {
                    sleep(50);
                }
                break;
        }
    }

    private static void initData(Context context) {
        okCard = null;
        recipientList = null;
        positions = new ArrayList<>();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(), OKCardsWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ok_cards);

        Query query = OKCardHelper.getOKCard();

        if ( query != null ) {

            query.get().addOnSuccessListener(querySnapshot ->
            {
                if ( !querySnapshot.isEmpty() ) {
                    while (idOKCard.isEmpty()) {
                        sleep(50);
                    }

                    initOK = true;

                    setData(context);

                    context.sendBroadcast(createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_NEXT));

                } else {
                    initOK = true;
                }
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
                                    views.setViewVisibility(R.id.widget_ok_cards_btn_left, View.VISIBLE);
                                    views.setViewVisibility(R.id.widget_ok_cards_btn_right, View.VISIBLE);

                                    appWidgetManager.updateAppWidget(componentName, views);
                                }

                                break;
                            case MODIFIED:
                                if ( okCard != null && dc.getDocument().getId().equals(okCard.getId()) ) {
                                    okCard = dc.getDocument().toObject(OKCard.class);

                                    setData(context);

                                    setOKCard(context, views);

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
        } else {
            initOK = true;
        }
    }

    private static Intent createIntentNextOrPreviousOkCard(Context context, int[] appWidgetIds, String extraDirection) {
        Intent nextIntent = new Intent(context, OKCardsWidget.class);

        nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        nextIntent.putExtra(EXTRA_DIRECTION, extraDirection);
        nextIntent.putExtra(EXTRA_INDICE, indice);

        switch (extraDirection) {
            case EXTRA_NEXT:
                Uri dataNext = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_cards_btn_left));
                nextIntent.setData(dataNext);
                break;
            case EXTRA_PREVIOUS:
                Uri dataPrevious = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_cards_btn_left));
                nextIntent.setData(dataPrevious);
                break;
        }

        return nextIntent;
    }

    private static void setOKCard(Context context, RemoteViews views) {
        views.setTextViewText(R.id.widget_ok_cards_name, okCard.getName());
        views.setTextViewText(R.id.widget_ok_cards_message, okCard.getMessage());

        views.setImageViewResource(R.id.widget_ok_cards_image, R.drawable.ic_logo); // TODO changer
    }

    private static void setRecipientList(RemoteViews views) {
        StringBuilder sb = new StringBuilder();

        for (String s : recipientList.getRecipients()) {
            sb.append(s).append("\n");
        }

        views.setTextViewText(R.id.widget_ok_cards_recipient_list_name, recipientList.getName());
        views.setTextViewText(R.id.widget_ok_cards_recipient_list, sb.toString());
    }

    private static void setPositions(RemoteViews views) {
        views.setViewVisibility(R.id.widget_ok_cards_wifi, View.GONE);
        views.setViewVisibility(R.id.widget_ok_cards_gps, View.GONE);

        for (Position position : positions) {
            if ( position.isWifi() ) {
                views.setViewVisibility(R.id.widget_ok_cards_wifi, View.VISIBLE);
                views.setTextViewText(R.id.widget_ok_cards_wifi_name, position.getName());
            } else {
                views.setViewVisibility(R.id.widget_ok_cards_gps, View.VISIBLE);
                views.setTextViewText(R.id.widget_ok_cards_gps_name, position.getName());
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            // On récupère le RemoteViews qui correspond à l'AppWidget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ok_cards);

            if ( !initOK ) {
                views.setViewVisibility(R.id.widget_ok_cards, View.GONE);
                views.setViewVisibility(R.id.widget_ok_cards_empty, View.GONE);
                views.setViewVisibility(R.id.widget_ok_cards_sync, View.VISIBLE);
            } else {
                if ( idOKCard.isEmpty() || okCard == null ) {
                    views.setViewVisibility(R.id.widget_ok_cards, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_cards_sync, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_cards_empty, View.VISIBLE);

                    if ( Utils.getCurrentUser() != null ) {
                        views.setViewVisibility(R.id.widget_ok_cards_connection, View.GONE);
                        views.setViewVisibility(R.id.widget_ok_cards_create, View.VISIBLE);

                        // La section suivante est destinée à l'ouverture d'une OKCard
                        // L'intent ouvre cette classe même…
                        Intent createIntent = new Intent(context, OKCardsWidget.class);

                        // Action l'action ACTION_OPEN_OKCARD
                        createIntent.setAction(ACTION_CREATE_OKCARD);

                        Uri dataCreate = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_cards_create));
                        createIntent.setData(dataCreate);

                        // On ajoute l'intent dans un PendingIntent
                        PendingIntent createPending = PendingIntent.getBroadcast(context, 5, createIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        views.setOnClickPendingIntent(R.id.widget_ok_cards_create, createPending);

                    } else {

                        views.setViewVisibility(R.id.widget_ok_cards_create, View.GONE);
                        views.setViewVisibility(R.id.widget_ok_cards_connection, View.VISIBLE);

                        // La section suivante est destinée à l'ouverture d'une OKCard
                        // L'intent ouvre cette classe même…
                        Intent connectionIntent = new Intent(context, OKCardsWidget.class);

                        // Action l'action ACTION_CONNECTION
                        connectionIntent.setAction(ACTION_CONNECTION);

                        connectionIntent.putExtra(MainActivity.BUNDLE_KEY_CONNECTION_ASK, true);

                        Uri dataConnection = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_cards_connection));
                        connectionIntent.setData(dataConnection);

                        // On ajoute l'intent dans un PendingIntent
                        PendingIntent connectionPending = PendingIntent.getBroadcast(context, 4, connectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        views.setOnClickPendingIntent(R.id.widget_ok_cards_connection, connectionPending);
                    }
                } else {
                    views.setViewVisibility(R.id.widget_ok_cards_empty, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_cards_sync, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_cards, View.VISIBLE);

                    if ( idOKCard.size() > 1 ) {
                        views.setViewVisibility(R.id.widget_ok_cards_btn_left, View.VISIBLE);
                        views.setViewVisibility(R.id.widget_ok_cards_btn_right, View.VISIBLE);
                    } else {
                        views.setViewVisibility(R.id.widget_ok_cards_btn_left, View.GONE);
                        views.setViewVisibility(R.id.widget_ok_cards_btn_right, View.GONE);
                    }

                    setOKCard(context, views);
                    setRecipientList(views);
                    setPositions(views);

                    // La section suivante est destinée à l'ouverture d'une OKCard
                    // L'intent ouvre cette classe même…
                    Intent openIntent = new Intent(context, OKCardsWidget.class);

                    // Action l'action ACTION_OPEN_OKCARD
                    openIntent.setAction(ACTION_OPEN_OKCARD);
                    // Et l'id de l'OKCard a ouvrir
                    openIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
                    openIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL, okCard.getUrlPicture());

                    Uri dataOpen = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_cards_image));
                    openIntent.setData(dataOpen);

                    // On ajoute l'intent dans un PendingIntent
                    PendingIntent openPending = PendingIntent.getBroadcast(context, 3, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_ok_cards_name, openPending);
                    views.setOnClickPendingIntent(R.id.widget_ok_cards_image, openPending);

                    // La section suivante est destinée à l'envoie d'un message d'une OKCard
                    // L'intent ouvre cette classe même…
                    Intent sendIntent = new Intent(context, OKCardsWidget.class);

                    // Action l'action ACTION_OPEN_OKCARD
                    sendIntent.setAction(ACTION_SEND);
                    // Et l'id de l'OKCard a ouvrir
                    sendIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());

                    Uri dataSend = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_cards_send));
                    sendIntent.setData(dataSend);

                    // On ajoute l'intent dans un PendingIntent
                    PendingIntent sendPending = PendingIntent.getBroadcast(context, 2, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_ok_cards_send, sendPending);
                }
            }

            //*******************NEXT*********************************
            // On insère l'intent dans un PendingIntent
            PendingIntent nextPending = PendingIntent.getBroadcast(context, 1,
                    createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_NEXT),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Et on l'associe à l'activation du bouton
            views.setOnClickPendingIntent(R.id.widget_ok_cards_btn_right, nextPending);

            //*******************PREVIOUS*****************************
            PendingIntent previousPending = PendingIntent.getBroadcast(context, 0,
                    createIntentNextOrPreviousOkCard(context, appWidgetIds, EXTRA_PREVIOUS),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_ok_cards_btn_left, previousPending);

            // Et il faut mettre à jour toutes les vues
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {

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

        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Si l'action est celle d'ouverture de carte
        if ( ACTION_CREATE_OKCARD.equals(intent.getAction()) ) {

            Intent intentOKCardActivity = new Intent(context.getApplicationContext(), OKCardActivity.class);

            TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(intentOKCardActivity)
                    .startActivities();

        } else if ( ACTION_OPEN_OKCARD.equals(intent.getAction()) ) {

            if ( intent.getExtras() != null ) {
                Intent intentOpenOKCard = new Intent(context.getApplicationContext(), OKCardActivity.class);
                intentOpenOKCard.putExtras(intent.getExtras());

                TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(intentOpenOKCard)
                        .startActivities();
            }

        } else if ( ACTION_SEND.equals(intent.getAction()) ) {

            if ( EasyPermissions.hasPermissions(context, Utils.Permission.SEND_SMS.name) ) {
                Utils.sendMessage(intent.getExtras().getString(OKCardActivity.BUNDLE_KEY_OK_CARD_ID));

                Toast.makeText(context, "Message envoyé", Toast.LENGTH_LONG).show();
            }

        } else if ( ACTION_CONNECTION.equals(intent.getAction()) ) {

            if ( intent.getExtras() != null ) {
                Intent intentMainActivity = new Intent(context.getApplicationContext(), MainActivity.class);
                intentMainActivity.putExtras(intent.getExtras());

                TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(intentMainActivity)
                        .startActivities();
            }

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
}
