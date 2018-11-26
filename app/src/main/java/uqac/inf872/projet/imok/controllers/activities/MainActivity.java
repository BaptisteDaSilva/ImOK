package uqac.inf872.projet.imok.controllers.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.UserHelper;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.utils.Utils;

public class MainActivity extends BaseActivity {

    //FOR DATA
    private static final int RC_SIGN_IN = 123;
    //FOR DESIGN
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.main_activity_button_login)
    Button buttonLogin;

//    @Override
//    protected void setDataBinding(ViewDataBinding mDataBinding) {
//
//    }


    private static final String PERMS_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_ACCESS_FINE_LOCATION_PERMS = 200;
    private static final String PROX_ALERT_INTENT = "uqac.inf872.projet.imok.receiver.ProximityAlert";
    private static final long POINT_RADIUS = 100; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( EasyPermissions.hasPermissions(this, PERMS_ACCESS_FINE_LOCATION) ) {
            addProximityAlert(48.42630690000001, -71.05366229999998); // Appartement
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_sens_message), RC_ACCESS_FINE_LOCATION_PERMS, PERMS_ACCESS_FINE_LOCATION);
        }

        addProximityAlert(48.420329, -71.05264999999997); // UQAC
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_ACCESS_FINE_LOCATION_PERMS)
    private void addProximityAlert(double latitude, double longitude) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no                           expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateUIWhenResuming();
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton() {
        if ( Utils.isCurrentUserLogged() ) {
            this.startProfileActivity();
        } else {
            this.startSignInActivity();
        }
    }

    @OnClick(R.id.main_activity_button_menu)
    public void onClickMenuButton() {
        if ( Utils.isCurrentUserLogged() ) {
            this.startMenuActivity();
        } else {
            showSnackBar(this.coordinatorLayout, getString(R.string.error_not_connected));
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore() {

        if ( Utils.getCurrentUser() != null ) {

            String urlPicture = (Utils.getCurrentUser().getPhotoUrl() != null) ? Utils.getCurrentUser().getPhotoUrl().toString() : null;
            String username = Utils.getCurrentUser().getDisplayName();
            String uid = Utils.getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(Utils.onFailureListener(getApplicationContext()));
        }
    }

    // --------------------
    // NAVIGATION
    // --------------------

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.ImOK_LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(), //GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo)
                        .build(),
                RC_SIGN_IN);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void startMenuActivity() {
        Intent intent = new Intent(this, MenuViewPagerActivity.class);
        startActivity(intent);
    }

    // --------------------
    // UI
    // --------------------

    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void updateUIWhenResuming() {
        this.buttonLogin.setText(Utils.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    // --------------------
    // UTILS
    // --------------------

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if ( requestCode == RC_SIGN_IN ) {
            if ( resultCode == RESULT_OK ) { // SUCCESS
                this.createUserInFirestore();
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if ( response == null ) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if ( response.getError().getErrorCode() == ErrorCodes.NO_NETWORK ) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if ( response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR ) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }
}
