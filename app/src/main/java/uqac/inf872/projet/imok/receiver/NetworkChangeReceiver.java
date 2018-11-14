package uqac.inf872.projet.imok.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import uqac.inf872.projet.imok.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public int ID_NOTIFICATION = 1;

    public String CHANNEL_ID = "my_channel_01";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        createNotificationChannel(context);

        NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if ( ConnectivityManager.TYPE_WIFI == netInfo.getType() ) {
            if ( netInfo.isConnected() ) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.twotone_thumb_up_alt_black_18)
                        .setContentTitle("Modification du WIFI")
                        .setContentText("SSID : " + ssid)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // Récupération du Notification Manager
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(ID_NOTIFICATION, mBuilder.build());
            }
        }
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
