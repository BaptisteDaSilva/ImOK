package uqac.inf872.projet.imok.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    // Intitulé de l'extra qui contient la direction du défilé
    private final static String EXTRA_DIRECTION = "extraDirection";

    // La valeur pour défiler vers la gauche
    private final static String EXTRA_PREVIOUS = "previous";

    // La valeur pour défiler vers la droite
    private final static String EXTRA_NEXT = "next";

    // Intitulé de l'extra qui contient l'indice actuel dans le tableau des tutos
    private final static String EXTRA_INDICE = "extraIndice";

    // Action qui indique qu'on essaie d'ouvrir un tuto sur internet
    private final static String ACTION_OPEN_OKCARD = "uqac.inf872.projet.imok.receiver.widget.action.ACTION_OPEN_OKCARD";

    private static ArrayList<String> idOKCard = new ArrayList<>();

    private static OKCard okCard;

    private static RecipientList recipientList;

    private static ArrayList<Position> positions = new ArrayList<>();

    private static ListenerRegistration registration;

    static Task taskRecipientList;
    static Task taskPosition;
    static Task taskPosition2;
    private static Executor setDataExecutor = Executors.newSingleThreadExecutor();
    // Indice actuel dans le tableau
    private int indice = 0;

    private static void setData(int indice) {
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
        }).addOnFailureListener(e -> Log.e(Utils.TAG, "setData - " + indice + " - " + e.getMessage()));

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
        idOKCard = new ArrayList<>();

        okCard = null;
        recipientList = null;
        positions = new ArrayList<>();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(), OKCardWidget.class.getName());

        Query query = OKCardHelper.getOKCard();

        if ( query != null ) {
            query.get().addOnSuccessListener(querySnapshot ->
            {
                setData(0);

                Intent nextIntent = new Intent(context, OKCardWidget.class);

                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

                nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                nextIntent.putExtra(EXTRA_DIRECTION, EXTRA_NEXT);
                nextIntent.putExtra(EXTRA_INDICE, 0);

                Uri data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_btn_left));
                nextIntent.setData(data);

                context.sendBroadcast(nextIntent);
            });

            registration = query.addSnapshotListener((value, e) -> {
                if ( e != null ) {
                    Utils.onFailureListener(context, e);
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            idOKCard.add(dc.getDocument().getId());
                            break;
                        case MODIFIED:
                            okCard = dc.getDocument().toObject(OKCard.class);

                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                            setOKCard(views);

                            views.setTextViewText(R.id.widget_ok_card_message, okCard.getMessage());

                            appWidgetManager.updateAppWidget(componentName, views);

                            break;
                        case REMOVED:
                            idOKCard.remove(dc.getDocument().getId());
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        initData(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
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
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // On récupère le RemoteViews qui correspond à l'AppWidget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            if ( !idOKCard.isEmpty() && okCard != null ) {
                views.setViewVisibility(R.id.widget_ok_card_error, View.GONE);
                views.setViewVisibility(R.id.widget_ok_card, View.VISIBLE);

                setOKCard(views);
                setRecipientList(views);
                setPositions(views);

                // La section suivante est destinée à l'ouverture d'une OKCard
                // L'intent ouvre cette classe même…
                Intent linkIntent = new Intent(context, OKCardWidget.class);

                // Action l'action ACTION_OPEN_OKCARD
                linkIntent.setAction(ACTION_OPEN_OKCARD);
                // Et l'id de l'OKCard a ouvrir
                linkIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
                linkIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL, okCard.getUrlPicture());

                // On ajoute l'intent dans un PendingIntent
                PendingIntent linkPending = PendingIntent.getBroadcast(context, 2, linkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.widget_ok_card_name, linkPending);
                views.setOnClickPendingIntent(R.id.widget_ok_card_image, linkPending);
            } else {
                views.setViewVisibility(R.id.widget_ok_card, View.GONE);
                views.setViewVisibility(R.id.widget_ok_card_error, View.VISIBLE);

                // La section suivante est destinée à l'ouverture d'une OKCard
                // L'intent ouvre cette classe même…
                Intent linkIntent = new Intent(context, OKCardWidget.class);

                // Action l'action ACTION_OPEN_OKCARD
                linkIntent.setAction(ACTION_OPEN_OKCARD);

                // On ajoute l'intent dans un PendingIntent
                PendingIntent linkPending = PendingIntent.getBroadcast(context, 2, linkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.widget_ok_card_create, linkPending);
            }

            //********************************************************
            //*******************NEXT*********************************
            //********************************************************
            Intent nextIntent = new Intent(context, OKCardWidget.class);

            // On veut que l'intent lance la mise à jour
            nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            // On n'oublie pas les identifiants
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            // On rajoute la direction
            nextIntent.putExtra(EXTRA_DIRECTION, EXTRA_NEXT);

            // Ainsi que l'indice
            nextIntent.putExtra(EXTRA_INDICE, indice);

            // Les données inutiles mais qu'il faut rajouter
            Uri data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_btn_right));
            nextIntent.setData(data);

            // On insère l'intent dans un PendingIntent
            PendingIntent nextPending = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Et on l'associe à l'activation du bouton
            views.setOnClickPendingIntent(R.id.widget_btn_right, nextPending);

            //********************************************************
            //*******************PREVIOUS*****************************
            //********************************************************

            Intent previousIntent = new Intent(context, OKCardWidget.class);

            previousIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            previousIntent.putExtra(EXTRA_DIRECTION, EXTRA_PREVIOUS);
            previousIntent.putExtra(EXTRA_INDICE, indice);

            data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_btn_left));
            previousIntent.setData(data);

            PendingIntent previousPending = PendingIntent.getBroadcast(context, 1, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_btn_left, previousPending);

            // Et il faut mettre à jour toutes les vues
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        if ( registration != null ) {
            // Stop listening to changes
            registration.remove();
        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Si l'action est celle d'ouverture de carte
        if ( ACTION_OPEN_OKCARD.equals(intent.getAction()) ) {
            Intent intentOKCardActivity = new Intent(context.getApplicationContext(), OKCardActivity.class);

            if ( intent.getExtras() != null ) {
                intentOKCardActivity.putExtras(intent.getExtras());
            }

            context.startActivity(intentOKCardActivity);
        } else {
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

                setData(indice);
            }
        }

        // On revient au traitement naturel du Receiver, qui va lancer onUpdate s'il y a demande de mise à jour
        super.onReceive(context, intent);
    }
}
