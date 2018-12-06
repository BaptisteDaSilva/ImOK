package uqac.inf872.projet.imok.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.utils.ButtonNotification;
import uqac.inf872.projet.imok.utils.Utils;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ( WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) ) {

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if ( networkInfo != null && networkInfo.isConnected() ) {
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                String ssid = wifiInfo.getSSID();

                CharSequence channelName = context.getString(R.string.channel_name_position);

                String description = context.getString(R.string.channel_description_position);

                Query query = PositionHelper.getPositionWifiWithSSID(ssid.replace("\"", ""));

                if ( query != null ) {
                    query.get().addOnCompleteListener(task -> {
                        if ( task.isSuccessful() ) {
                            for (QueryDocumentSnapshot documentWifi : task.getResult()) {
                                Position positionWifi = documentWifi.toObject(Position.class);

                                OKCardHelper.getOKCardPosition(positionWifi.getId()).get().addOnCompleteListener(taskOKCard -> {
                                    if ( taskOKCard.isSuccessful() ) {
                                        for (QueryDocumentSnapshot document : taskOKCard.getResult()) {
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

                                            Utils.addReceiverForAction(context);

                                            // TODO inutile pour les tablettes sans SMS
                                            Intent intentSend = new Intent(ActionReceiver.ACTION_SEND);
                                            intentSend.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
                                            intentSend.putExtra(ActionReceiver.BUNDLE_KEY_TYPE, ActionReceiver.BUNDLE_KEY_TYPE_WIFI);

                                            PendingIntent pendingIntentSend = PendingIntent.getBroadcast(context, 10, intentSend, PendingIntent.FLAG_UPDATE_CURRENT);

                                            ButtonNotification buttonSend = new ButtonNotification(R.drawable.ic_logo, R.string.send, pendingIntentSend); // TODO changer

                                            // Show notification after received message
                                            Utils.sendVisualNotification(context, channelName, importance, description, "WIFI", "Connexion au wifi " + positionWifi.getName() + " detecté !!!", "Wifi SSID = " + ssid, pendingIntent, buttonSend);
                                        }
                                    } else {
                                        Utils.onFailureListener(context, taskOKCard.getException());
                                    }
                                });
                            }
                        } else {
                            Utils.onFailureListener(context, task.getException());
                        }
                    });
                }
            }
        }
    }
}
