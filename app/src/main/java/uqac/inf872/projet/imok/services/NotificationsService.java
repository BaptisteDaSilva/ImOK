package uqac.inf872.projet.imok.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.controllers.activities.MainActivity;
import uqac.inf872.projet.imok.utils.Utils;

public class NotificationsService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if ( notification != null ) {
            String titre = notification.getTitle();
            String message = notification.getBody();

            // Create an Intent that will be shown when user will click on the Notification
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            CharSequence channelName = getString(R.string.channel_name_firebase_messaging_service);

            int importance = -1;

            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ) {
                importance = NotificationManager.IMPORTANCE_HIGH;
            }

            String description = getString(R.string.channel_description_firebase_messaging_service);

            // Show notification after received message
            Utils.sendVisualNotification(this, channelName, importance, description, "NOTIFICATION_FIREBASE_MESSAGING", titre, message, pendingIntent);
        }
    }
}
