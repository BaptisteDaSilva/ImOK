package uqac.inf872.projet.imok.controllers.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.List;

import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;

public class TestActivity extends BaseActivity {

    private static final String PERMS_CAMERA = Manifest.permission.CAMERA;
    private static final int RC_CAMERA = 100;

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> liste = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        Log.e("I'm OK", "-------------------------------------");
        Log.e("I'm OK", "--------------- Debut ---------------");
        Log.e("I'm OK", "-------------------------------------");

        for (Sensor s : liste) {
            Log.e("I'm OK", s.getName());
        }

        Log.e("I'm OK", "-------------------------------------");
        Log.e("I'm OK", "---------------- Fin ----------------");
        Log.e("I'm OK", "-------------------------------------");
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @OnClick(R.id.envoyerSMS)
    public void onClickEnvoyerSMS(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, SendMessageActivity.class));
    }

    public void onClickTelephoner(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, TelephoneActivity.class));
    }

    public void onClickWifi(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, WiFiActivity.class));
    }

    public void onClickGPS(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, GPSActivity.class));
    }

    public void onClickBD(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, BDActivity.class));
    }

    public void onClickProgressBar(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, ProgressBarActivity.class));
    }

    public void onClickProgressBarAsync(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, ProgressBarAsyncActivity.class));
    }

    public void onClickFragment(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, FragmentActivity.class));
    }

    public void onClickNotification(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, NotificationActivity.class));
    }

    public void onClickMaps(View view) {
        TestActivity.this.startActivity(new Intent(TestActivity.this, MapsActivity.class));
    }

    public void onClickCamera(View view) {
        if ( EasyPermissions.hasPermissions(this, PERMS_CAMERA) ) {
            launchCameraActivity();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_camera), RC_CAMERA, PERMS_CAMERA);
        }
    }

    @AfterPermissionGranted(RC_CAMERA)
    private void launchCameraActivity() {
        TestActivity.this.startActivity(new Intent(TestActivity.this, CameraActivity.class));
    }
}
