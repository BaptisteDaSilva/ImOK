package uqac.inf872.projet.imok.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.utils.ButtonNotification;
import uqac.inf872.projet.imok.utils.Utils;

public class ProximityAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ( intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false) ) {
            String name = intent.getStringExtra(Utils.PROXIMITY_ALERT_INTENT_EXTRA_NAME);

            CharSequence channelName = context.getString(R.string.channel_name_position);

            String description = context.getString(R.string.channel_description_position);

            Query query = OKCardHelper.getOKCardPosition(intent.getStringExtra(Utils.PROXIMITY_ALERT_INTENT_EXTRA_ID));

            if ( query != null ) {
                query.get().addOnCompleteListener(task -> {
                    if ( task.isSuccessful() ) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OKCard okCard = document.toObject(OKCard.class);

                            int importance = -1;

                            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ) {
                                importance = NotificationManager.IMPORTANCE_HIGH;
                            }

                            // Create an Intent that will be shown when user will click on the Notification
                            Intent intentOKCard = new Intent(context, OKCardActivity.class);
                            intentOKCard.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
                            intentOKCard.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL, okCard.getUrlPicture());
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentOKCard, PendingIntent.FLAG_ONE_SHOT);

                            // TODO inutile pour les tablettes sans SMS
                            Intent intentSend = new Intent(ActionReceiver.ACTION_SEND);
                            intentSend.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
                            intentSend.putExtra(ActionReceiver.BUNDLE_KEY_TYPE, ActionReceiver.BUNDLE_KEY_TYPE_PROXIMITY_ALERT);

                            PendingIntent pendingIntentSend = PendingIntent.getBroadcast(context, 15, intentSend, PendingIntent.FLAG_UPDATE_CURRENT);

                            ButtonNotification buttonSend = new ButtonNotification(R.drawable.ic_send_black, R.string.send, pendingIntentSend);

                            // Show notification after received message
                            Utils.sendVisualNotification(context, channelName, importance, description, "GPS", "Location alert - Entering " + name, "Localisation " + name, pendingIntent, buttonSend);
                        }
                    } else {
                        Utils.onFailureListener(context, task.getException());
                    }
                });
            }
        }
    }
}