package uqac.inf872.projet.imok.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.api.RecipientListHelper;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.MainActivity;
import uqac.inf872.projet.imok.controllers.activities.MenuViewPagerActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.receiver.ActionReceiver;
import uqac.inf872.projet.imok.receiver.ProximityAlertReceiver;
import uqac.inf872.projet.imok.receiver.WifiReceiver;

public class Utils {

    public static final String TAG = "I_M_OK";

    public static final int RC_SIGN_IN = 100;

    // PERMISSIONS
    public static final int PERMISSION_INTERNET_RC = 1;
    public static final int PERMISSION_ACCESS_FINE_LOCATION_RC = 2;
    public static final int PERMISSION_ACCESS_WIFI_STATE_RC = 3;
    public static final int PERMISSION_SEND_SMS_RC = 4;
    public static final String PROX_ALERT_INTENT = "uqac.inf872.projet.imok.receiver.ProximityAlert";
    public static final String PROX_ALERT_INTENT_EXTRA_ID = "uqac.inf872.projet.imok.receiver.ProximityAlert.extra.id";
    public static final String PROX_ALERT_INTENT_EXTRA_NAME = "uqac.inf872.projet.imok.receiver.ProximityAlert.extra.name";

    // NOTIFICATION TAG AND ID
    public static final int NOTIFICATION_ID_FIREBASE_MESSAGING = 1;
    public static final String NOTIFICATION_TAG_FIREBASE_MESSAGING = "FIREBASE_MESSAGING";
    public static final int NOTIFICATION_ID_WIFI = 2;
    public static final String NOTIFICATION_TAG_WIFI = "WIFI";
    public static final int NOTIFICATION_ID_PROXIMITY_ALERT = 3;
    public static final String NOTIFICATION_TAG_PROXIMITY_ALERT = "GPS";

    // BROADCAST RECEIVER
    private static BroadcastReceiver wifiReceiver;
    private static BroadcastReceiver proximityAlertReceiver;
    private static BroadcastReceiver actionReceiver;

    public static void askAllPermissions(MainActivity mainActivity) {
        for (Permission perm : Permission.values()) {
            isGrantedPermission(mainActivity, perm);
        }
    }

    // --------------------
    // ACTION
    // --------------------

    private static String getMessageError(Context context, int requestCode) {
        String message = context.getString(R.string.popup_ask_all_permissions);

        switch (requestCode) {
            case PERMISSION_INTERNET_RC:
                message = context.getString(R.string.popup_ask_permission_internet);
                break;
            case PERMISSION_ACCESS_FINE_LOCATION_RC:
                message = context.getString(R.string.popup_ask_permission_access_fine_location);
                break;
            case PERMISSION_ACCESS_WIFI_STATE_RC:
                message = context.getString(R.string.popup_ask_permission_wifi_state);
                break;
            case PERMISSION_SEND_SMS_RC:
                message = context.getString(R.string.popup_ask_permission_send_sms);
        }

        return message;
    }

    public static boolean isGrantedPermission(BaseActivity activity, Permission perm) {

        if ( !EasyPermissions.hasPermissions(activity, perm.name) ) {

            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(activity, perm.requestCode, perm.name)
                            .setRationale(getMessageError(activity.getApplicationContext(), perm.requestCode))
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
                            .build());

            return false;
        }

        return true;
    }

    public static boolean isGrantedPermission(BaseFragment fragment, Permission perm) {

        if ( !EasyPermissions.hasPermissions(fragment.getActivity(), perm.name) ) {

            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(fragment, perm.requestCode, perm.name)
                            .setRationale(getMessageError(fragment.getContext(), perm.requestCode))
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
                            .setTheme(R.style.ImOK)
                            .build());

            return false;
        }

        return true;
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    public static void openMenu(Context context, Menu menu) {
        Intent intent = new Intent(context, MenuViewPagerActivity.class);
        intent.putExtra(MenuViewPagerActivity.BUNDLE_KEY_MENU_ID, menu.id);

        context.startActivity(intent);
    }

    // --------------------
    // UTILS
    // --------------------

    public static void sendMessage(String idOKCard) {
        OKCardHelper.getOKCard(idOKCard).get()
                .addOnSuccessListener(documentSnapshotOKCard ->
                {
                    OKCard currentOKCard = documentSnapshotOKCard.toObject(OKCard.class);

                    sendMessage(currentOKCard.getMessage(), currentOKCard.getIdListe());
                });
    }

    public static void sendMessage(String msg, String idRecipientList) {

        SmsManager manager = SmsManager.getDefault();

        RecipientListHelper.getRecipientList(idRecipientList)
                .addOnSuccessListener(documentSnapshotRecipient ->
                {
                    RecipientList recipientList = documentSnapshotRecipient.toObject(RecipientList.class);

                    for (String num : recipientList.getRecipients()) {

                        manager.sendTextMessage(num, null, msg, null, null); // piSend, piDelivered);
                    }
                });
    }

    public static OnFailureListener onFailureListener(Context context) {
        return e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    public static void onFailureListener(Context context, Exception e) {
        Log.e(Utils.TAG, "Exception : " + e.getMessage());

        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Boolean isCurrentUserLogged() {
        return (getCurrentUser() != null);
    }

    public static void sendVisualNotification(Context context, CharSequence channelName, int importance, String description, String notification, String titre, String message, PendingIntent pendingIntent, ButtonNotification... buttons) {

        // Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(titre);
        inboxStyle.addLine(message);

        // Create a Channel (Android 8)
        String channelId = context.getString(R.string.default_notification_channel_id);

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_ok)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if ( notificationManager != null ) {

            // Support Version >= Android 8
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                channel.setDescription(description);

                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }

            for (ButtonNotification button : buttons) {
                notificationBuilder.addAction(button.getLogo(), context.getString(button.getNom()), button.getAction());
            }

            switch (notification) {
                case "NOTIFICATION_FIREBASE_MESSAGING":
                    // Show notification
                    notificationManager.notify(NOTIFICATION_TAG_FIREBASE_MESSAGING, NOTIFICATION_ID_FIREBASE_MESSAGING, notificationBuilder.build());
                    break;
                case "WIFI":
                    // Show notification
                    notificationManager.notify(NOTIFICATION_TAG_WIFI, NOTIFICATION_ID_WIFI, notificationBuilder.build());
                    break;
                case "GPS":
                    // Show notification
                    notificationManager.notify(NOTIFICATION_TAG_PROXIMITY_ALERT, NOTIFICATION_ID_PROXIMITY_ALERT, notificationBuilder.build());
                    break;
            }
        }
    }

    public static void addReceiverForAction(Context context) {
        if ( actionReceiver == null ) {
            actionReceiver = new ActionReceiver();

            IntentFilter filter = new IntentFilter(ActionReceiver.ACTION_SEND);
            context.registerReceiver(actionReceiver, filter);
        }
    }

    public static void addReceiverForWifi(Context context) {
        if ( wifiReceiver == null ) {
            wifiReceiver = new WifiReceiver();

            IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            context.registerReceiver(wifiReceiver, filter);
        }
    }

    public static void unregisterReceiverForWifi(Context context) {
        if ( wifiReceiver != null ) {
            context.unregisterReceiver(wifiReceiver);
        }
    }

    public static void addReceiverForProximityAlert(Context context) {
        if ( proximityAlertReceiver == null ) {
            proximityAlertReceiver = new ProximityAlertReceiver();

            IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
            context.registerReceiver(proximityAlertReceiver, filter);
        }
    }

    public static void unregisterReceiverForProximityAlert(Context context) {
        if ( proximityAlertReceiver != null ) {
            context.unregisterReceiver(proximityAlertReceiver);
        }
    }

    public enum Permission {
        INTERNET(PERMISSION_INTERNET_RC, Manifest.permission.INTERNET),
        ACCESS_FINE_LOCATION(PERMISSION_ACCESS_FINE_LOCATION_RC, Manifest.permission.ACCESS_FINE_LOCATION),
        ACCESS_WIFI_STATE(PERMISSION_ACCESS_WIFI_STATE_RC, Manifest.permission.ACCESS_WIFI_STATE),
        SEND_SMS(PERMISSION_SEND_SMS_RC, Manifest.permission.SEND_SMS);

        public final int requestCode;
        public final String name;

        Permission(int requestCode, String name) {

            this.requestCode = requestCode;
            this.name = name;
        }
    }

    public enum Menu {
        OKCard(0),
        RecipientList(1),
        Position(2);

        public final int id;

        Menu(int id) {
            this.id = id;
        }
    }
}
