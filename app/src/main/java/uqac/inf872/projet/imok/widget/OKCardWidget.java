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
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.api.RecipientListHelper;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.Utils;

import static android.os.SystemClock.sleep;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link OKCardWidgetConfigureActivity OKCardWidgetConfigureActivity}
 */
public class OKCardWidget extends AppWidgetProvider {

    // Action qui indique ce qu'on essaie de faire
    private final static String ACTION_SEND = "uqac.inf872.projet.imok.receiver.widget_ok_card.action.ACTION_SEND";
    // Task
    static Task taskRecipientList;
    // TODO ecrire
    private static HashMap<String, OKCard> okCards;
    private static HashMap<String, RecipientList> recipientLists;
    // Listener
    private static ListenerRegistration registrationOKCard;
    private static ListenerRegistration registrationRecipientList;

    // Executor
    private static Executor setDataExecutor = Executors.newSingleThreadExecutor();
    private static Executor initExecutor = Executors.newSingleThreadExecutor();

    static void getData(Context context, int appWidgetId) {

        String idOKCardPref = OKCardWidgetConfigureActivity.loadIdOKCardPref(context, appWidgetId);

        if ( !"".equals(idOKCardPref) ) {
            taskRecipientList = null;

            if ( !okCards.containsKey(idOKCardPref) ) {
                OKCardHelper.getOKCard(idOKCardPref).get().addOnSuccessListener(setDataExecutor, documentSnapshot ->
                {
                    OKCard okCard = documentSnapshot.toObject(OKCard.class);

                    okCards.put(okCard.getId(), okCard);

                    if ( !recipientLists.containsKey(okCard.getIdListe()) ) {
                        taskRecipientList = RecipientListHelper.getRecipientList(okCard.getIdListe())
                                .addOnSuccessListener(setDataExecutor, documentSnapshotRecipient ->
                                {
                                    RecipientList recipientList = documentSnapshotRecipient.toObject(RecipientList.class);

                                    recipientLists.put(recipientList.getId(), recipientList);
                                });
                    }

                }).addOnFailureListener(e -> Utils.onFailureListener(context, e));

                while (taskRecipientList == null || !taskRecipientList.isComplete()) {
                    sleep(50);
                }
            }
        }
    }

    private static void initData(Context context) {

        okCards = new HashMap<>();
        recipientLists = new HashMap<>();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(), OKCardWidget.class.getName());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ok_card);

        Query query = OKCardHelper.getOKCard();

        if ( query != null ) {

            registrationOKCard = query.addSnapshotListener(initExecutor, (value, e) -> {

                if ( e != null ) {
                    Utils.onFailureListener(context, e);
                    return;
                }

                if ( value != null ) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                break;
                            case MODIFIED:
                                if ( okCards.containsKey(dc.getDocument().getId()) ) {
                                    OKCard okCard = dc.getDocument().toObject(OKCard.class);

                                    okCards.put(okCard.getId(), okCard);

                                    setOKCard(views, okCard);

                                    // TODO faire marcher
//                                    setRecipientList(views, recipientList);
                                }

                                break;
                            case REMOVED:
                                okCards.remove(dc.getDocument().getId()); // TODO faire en sorte que la carte se refraichisse et affiche une erreur

                                Log.e("I'm OK", "Delete okCrad");

                                break;
                        }
                    }

                    // TODO update seulement les bons
//                    appWidgetManager.updateAppWidget(componentName, views);
                }
            });

            registrationRecipientList = RecipientListHelper.getRecipientList().addSnapshotListener(initExecutor, (value, e) -> {

                if ( e != null ) {
                    Utils.onFailureListener(context, e);
                    return;
                }

                if ( value != null ) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                break;
                            case MODIFIED:
                                if ( recipientLists.containsKey(dc.getDocument().getId()) ) {
                                    RecipientList recipientList = dc.getDocument().toObject(RecipientList.class);

                                    recipientLists.put(recipientList.getId(), recipientList);

                                    setRecipientList(views, recipientList);
                                }

                                break;
                            case REMOVED:
                                recipientLists.remove(dc.getDocument().getId());

                                break;
                        }
                    }

                    // TODO update seulement les bons
                    // appWidgetManager.updateAppWidget(componentName, views);
                }
            });
        }
    }

    private static void setOKCard(RemoteViews views, OKCard okCard) {

        // On met les bon texte
        views.setTextViewText(R.id.widget_ok_card_name, okCard.getName());
        views.setTextViewText(R.id.widget_ok_card_message, okCard.getMessage());
    }

    private static void setRecipientList(RemoteViews views, RecipientList recipientList) {
        views.setTextViewText(R.id.widget_ok_card_recipient_list_name, recipientList.getName());
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String idOKCardPref = OKCardWidgetConfigureActivity.loadIdOKCardPref(context, appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ok_card);

        views.setViewVisibility(R.id.widget_ok_card_not_exist, View.GONE);
        views.setViewVisibility(R.id.widget_ok_card, View.GONE);
        views.setViewVisibility(R.id.widget_ok_card_sync, View.VISIBLE);

        if ( "".equals(idOKCardPref) ) {

            views.setViewVisibility(R.id.widget_ok_card, View.GONE);
            views.setViewVisibility(R.id.widget_ok_card_sync, View.GONE);
            views.setViewVisibility(R.id.widget_ok_card_not_exist, View.VISIBLE);

        } else {

            OKCard okCard = okCards.get(idOKCardPref);

            if ( okCard != null ) {

                RecipientList recipientList = recipientLists.get(okCard.getIdListe());

                if ( recipientList != null ) {

                    views.setViewVisibility(R.id.widget_ok_card_not_exist, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_card_sync, View.GONE);
                    views.setViewVisibility(R.id.widget_ok_card, View.VISIBLE);

                    setOKCard(views, okCard);
                    setRecipientList(views, recipientList);

                    // La section suivante est destinée à l'envoie d'un message d'une OKCard
                    // L'intent ouvre cette classe même…
                    Intent sendIntent = new Intent(context, OKCardWidget.class);

                    // Action l'action ACTION_SEND
                    sendIntent.setAction(ACTION_SEND);
                    // Et l'id de l'OKCard a ouvrir
                    sendIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, idOKCardPref);

                    Uri dataSend = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_ok_card_send));
                    sendIntent.setData(dataSend);

                    // On ajoute l'intent dans un PendingIntent
                    PendingIntent sendPending = PendingIntent.getBroadcast(context, appWidgetId, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_ok_card_send, sendPending);
                }
            }
        }

        // Et il faut mettre à jour toutes la vues
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            OKCardWidget.getData(context, appWidgetId);

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            OKCardWidgetConfigureActivity.deleteIdOKCardPref(context, appWidgetId);
        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
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
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if ( registrationOKCard == null || registrationRecipientList == null ) {
            initData(context);
        }

        if ( ACTION_SEND.equals(intent.getAction()) ) {

            if ( EasyPermissions.hasPermissions(context, Utils.Permission.SEND_SMS.name) ) {
                Utils.sendMessage(intent.getExtras().getString(OKCardActivity.BUNDLE_KEY_OK_CARD_ID));

                Toast.makeText(context, "Message envoyé", Toast.LENGTH_LONG).show();
            }
        }

        // On revient au traitement naturel du Receiver, qui va lancer onUpdate s'il y a demande de mise à jour
        super.onReceive(context, intent);
    }
}

