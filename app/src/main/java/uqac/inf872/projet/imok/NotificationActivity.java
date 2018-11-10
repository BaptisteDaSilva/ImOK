package uqac.inf872.projet.imok;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.perf.metrics.AddTrace;

public class NotificationActivity extends AppCompatActivity {
    public int ID_NOTIFICATION = 0;

    public String CHANNEL_ID = "my_channel_01";

    @Override
    @AddTrace(name = "test_trace")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        createNotificationChannel();

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_done_white_36)
                .setContentTitle("Titre de la notification")
                .setContentText("Cors de la notification")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("notification"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        Button b = findViewById(R.id.bouton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Récupération du Notification Manager
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(ID_NOTIFICATION, mBuilder.build());
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

