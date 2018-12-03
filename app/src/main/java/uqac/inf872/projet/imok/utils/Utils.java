package uqac.inf872.projet.imok.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
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

public class Utils {

    public static final String TAG = "i_m_ok";
    public static final int RC_SIGN_IN = 100;
    // PERMISSIONS
    public static final int PERMISSION_INTERNET_RC = 1;
    public static final int PERMISSION_ACCESS_FINE_LOCATION_RC = 2;
    public static final int PERMISSION_ACCESS_WIFI_STATE_RC = 3;
    public static final int PERMISSION_SEND_SMS_RC = 4;
    // REQUEST CODE
    private static final int RC_ALL_PERMS = 0;

    public static void askAllPermissions(MainActivity mainActivity) {
        // TODO réécrire en plus propre
        String[] perms = new String[4];

        int i = 0;

        for (Permission p : Permission.values()) {
            perms[i++] = p.name;
        }

        if ( !EasyPermissions.hasPermissions(mainActivity, perms) ) {

            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(mainActivity, RC_ALL_PERMS, perms)
                            .setRationale(R.string.popup_ask_all_permissions)
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
                            .build());
        }
    }

    public static boolean isGrantedPermission(BaseActivity activity, Permission perm) {

        if ( !EasyPermissions.hasPermissions(activity, perm.name) ) {
            int resId = activity.getResources().getIdentifier("popup_ask_permission_" + perm.requestCode, "string", activity.getPackageName());

            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(activity, perm.requestCode, perm.name)
                            .setRationale(activity.getString(resId))
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
                            .build());

            return false;
        }

        return true;
    }

    public static boolean isGrantedPermission(BaseFragment fragment, Permission perm) {

        if ( !EasyPermissions.hasPermissions(fragment.getActivity(), perm.name) ) {
            int resId = fragment.getResources().getIdentifier("popup_ask_permission_" + perm.requestCode, "string", fragment.getActivity().getPackageName());

            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(fragment, perm.requestCode, perm.name)
                            .setRationale(fragment.getString(resId))
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
                            .setTheme(R.style.ImOK)
                            .build());

            return false;
        }

        return true;
    }

    public static void openMenu(Context context, Menu menu) {
        Intent intent = new Intent(context, MenuViewPagerActivity.class);
        intent.putExtra(MenuViewPagerActivity.BUNDLE_KEY_MENU_ID, menu.id);

        context.startActivity(intent);
    }

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

    // --------------------
    // ACTION
    // --------------------

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    public static Boolean isCurrentUserLogged() {
        return (getCurrentUser() != null);
    }

    // --------------------
    // UTILS
    // --------------------

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
