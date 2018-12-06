package uqac.inf872.projet.imok.controllers.activities;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.UserHelper;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.utils.Utils;
import uqac.inf872.projet.imok.widget.OKCardsWidget;

public class MainActivity extends BaseActivity {

    public static final String BUNDLE_KEY_CONNECTION_ASK = "BUNDLE_KEY_CONNECTION_ASK";
    //    @Override
//    protected void setDataBinding(ViewDataBinding mDataBinding) {
//
//    }
    //FOR DESIGN
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_activity_button_login)
    Button buttonLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.askAllPermissions(this);

        Utils.addReceiverForAction(this);
        Utils.addReceiverForWifi(this);
        Utils.addReceiverForProximityAlert(this);

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            if ( extras.getBoolean(BUNDLE_KEY_CONNECTION_ASK, false) ) {
                this.startSignInActivity();
            }
        }

        if ( Utils.isGrantedPermission(this, Utils.Permission.ACCESS_FINE_LOCATION) ) {
            addAllProximityAlert();
        }
    }

    @AfterPermissionGranted(Utils.PERMISSION_ACCESS_FINE_LOCATION_RC)
    private void addAllProximityAlert() {
        addProximityAlert(48.42630690000001, -71.05366229999998, "0iBix0tXbVvLC7vBL9Yw", "Appart");
//        addProximityAlert(48.420329, -71.05264999999997, "HZ1TRIMGkxcABwvpclDG", "UQAC");
    }

    @SuppressLint("MissingPermission")
    public void addProximityAlert(double latitude, double longitude, String id, String name) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent("uqac.inf872.projet.imok.receiver.ProximityAlert");
        intent.putExtra(Utils.PROX_ALERT_INTENT_EXTRA_ID, id);
        intent.putExtra(Utils.PROX_ALERT_INTENT_EXTRA_NAME, name);

        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                5, // the radius of the central point of the alert region, in meters
                -1, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
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
            String email = Utils.getCurrentUser().getEmail();

            UserHelper.createUser(uid, username, email, urlPicture).addOnFailureListener(Utils.onFailureListener(getApplicationContext()));
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
                Utils.RC_SIGN_IN);
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

        if ( requestCode == Utils.RC_SIGN_IN ) {
            if ( resultCode == RESULT_OK ) { // SUCCESS
                this.createUserInFirestore();
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                ComponentName componentName = new ComponentName(getApplicationContext().getPackageName(), OKCardsWidget.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

                Intent refreshIntent = new Intent(getApplicationContext(), OKCardsWidget.class);

                refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

                sendBroadcast(refreshIntent);
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
