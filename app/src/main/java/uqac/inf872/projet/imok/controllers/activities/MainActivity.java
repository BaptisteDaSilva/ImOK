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
