package uqac.inf872.projet.imok.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //    ViewDataBinding mDataBinding;

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this); //Configure Butterknife

//        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
    }

//    protected abstract void  setDataBinding(ViewDataBinding mDataBinding);

    protected abstract int getFragmentLayout();

    // --------------------
    // UI
    // --------------------

    protected void configureToolbar() {
        // Get the toolbar (Serialise)
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Set the toolbar
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if ( ab != null ) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    // --------------------
    // PERMISSIONS
    // --------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(Utils.TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if ( EasyPermissions.somePermissionPermanentlyDenied(this, perms) ) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    // --------------------
    // MultiDex
    // --------------------

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
