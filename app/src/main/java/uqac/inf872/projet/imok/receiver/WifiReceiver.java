package uqac.inf872.projet.imok.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import uqac.inf872.projet.imok.R;

public class WifiReceiver extends BroadcastReceiver {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "WIFI";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
         Tested (I didn't test with the WPS "Wi-Fi Protected Setup" standard):
         In API15 (ICE_CREAM_SANDWICH) this method is called when the new Wi-Fi network state is:
         DISCONNECTED, OBTAINING_IPADDR, CONNECTED or SCANNING

         In API19 (KITKAT) this method is called when the new Wi-Fi network state is:
         DISCONNECTED (twice), OBTAINING_IPADDR, VERIFYING_POOR_LINK, CAPTIVE_PORTAL_CHECK
         or CONNECTED

         (Those states can be obtained as NetworkInfo.DetailedState objects by calling
         the NetworkInfo object method: "networkInfo.getDetailedState()")
        */
        /*
         * NetworkInfo object associated with the Wi-Fi network.
         * It won't be null when "android.net.wifi.STATE_CHANGE" action intent arrives.
         */
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if ( networkInfo != null && networkInfo.isConnected() ) {
            // TODO: Place the work here, like retrieving the access point's SSID

            /*
             * WifiInfo object giving information about the access point we are connected to.
             * It shouldn't be null when the new Wi-Fi network state is CONNECTED, but it got
             * null sometimes when connecting to a "virtualized Wi-Fi router" in API15.
             */
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            String ssid = wifiInfo.getSSID();

            // Create a Style for the Notification
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Wifi change");
            inboxStyle.addLine("Wifi SSID = " + ssid);

            // Create a Channel (Android 8)
            String channelId = context.getString(R.string.default_notification_channel_id);

            // Build a Notification object
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_ok)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.notification_title))
                            .setStyle(inboxStyle);

            // Add the Notification to the Notification Manager and show it.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Support Version >= Android 8
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                CharSequence channelName = "RecipientList provenant de Firebase";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            // Show notification
            notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
