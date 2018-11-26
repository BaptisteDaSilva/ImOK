package uqac.inf872.projet.imok.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import uqac.inf872.projet.imok.R;

public class ProximityAlertReceiver extends BroadcastReceiver {

    private final int NOTIFICATION_ID = 005;
    private final String NOTIFICATION_TAG = "GPS";

    @Override
    public void onReceive(Context context, Intent intent) {
        String locationManager = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(locationManager, false);

        // Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Location alert");
        inboxStyle.addLine(entering ? "Entering" : "Exiting");

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
