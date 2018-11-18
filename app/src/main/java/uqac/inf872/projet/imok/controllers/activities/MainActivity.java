package uqac.inf872.projet.imok.controllers.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.UserHelper;
import uqac.inf872.projet.imok.base.BaseActivity;

public class MainActivity extends BaseActivity {

    //FOR DATA
    private static final int RC_SIGN_IN = 123;
    //FOR DESIGN
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.main_activity_button_login)
    Button buttonLogin;

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
        if ( this.isCurrentUserLogged() ) {
            this.startProfileActivity();
        } else {
            this.startSignInActivity();
        }
    }

    @OnClick(R.id.main_activity_button_menu)
    public void onClickMenuButton() {
        if ( this.isCurrentUserLogged() ) {
            this.startMenuActivity();
        } else {
            showSnackBar(this.coordinatorLayout, getString(R.string.error_not_connected));
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore() {

        if ( this.getCurrentUser() != null ) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
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
        // TODO supprimer

//        String uid = this.getCurrentUser().getUid();
//
//        ArrayList<String> array = new ArrayList<>();
//        array.add("1");
//
//        OKCardHelper.createOKCard("Test",
//                "https://www.tuxboard.com/photos/2014/11/infographie-metiers-avocat-720x437.jpg",
//                "1", array, uid);
//
//        OKCardHelper.createOKCard("Test 2",
//                "https://www.tuxboard.com/photos/2014/11/infographie-metiers-espion.jpg",
//                "1", array, uid);
//
//        OKCardHelper.createOKCard("Test 3",
//                "https://www.tuxboard.com/photos/2014/11/infographie-metiers-dev.jpg",
//                "1", array, uid);
//
//                OKCardHelper.createOKCard("Test 3",
//                "https://www.tuxboard.com/photos/2014/11/infographie-metiers-dev.jpg",
//                "1", array, uid);
//
//        PositionHelper.createPositionWifi("Maison", "SSID", uid);
//        PositionHelper.createPositionGPS("Maison", 10.0, 10.0, 30, uid);
//
//        ArrayList<String> array2 = new ArrayList<>();
//        array2.add("07828117769");
//
//        RecipientListHelper.createRecipientList("Amis", array2, uid);

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
        this.buttonLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
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
