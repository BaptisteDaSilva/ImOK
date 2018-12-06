package uqac.inf872.projet.imok.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.utils.Utils;

public class ActionReceiver extends BroadcastReceiver {

    public final static String ACTION_SEND = "uqac.inf872.projet.imok.receiver.ActionReceiver.action.ACTION_SEND";
    public static final String BUNDLE_KEY_TYPE = "BUNDLE_KEY_TYPE";
    public static final String BUNDLE_KEY_TYPE_WIFI = "WIFI";
    public static final String BUNDLE_KEY_TYPE_PROWIMITY_ALERT = "PROXIMITY_ALERT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( ACTION_SEND.equals(intent.getAction()) ) {
            if ( EasyPermissions.hasPermissions(context, Utils.Permission.SEND_SMS.name) ) {
                Utils.sendMessage(intent.getStringExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID));

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                switch (intent.getStringExtra(BUNDLE_KEY_TYPE)) {
                    case BUNDLE_KEY_TYPE_WIFI:
                        manager.cancel(Utils.NOTIFICATION_TAG_WIFI, Utils.NOTIFICATION_ID_WIFI);
                        break;
                    case BUNDLE_KEY_TYPE_PROWIMITY_ALERT:
                        manager.cancel(Utils.NOTIFICATION_TAG_PROXIMITY_ALERT, Utils.NOTIFICATION_ID_PROXIMITY_ALERT);
                        break;
                }

                Toast.makeText(context, "Message envoyé", Toast.LENGTH_LONG).show();
            }
        }
    }
}
