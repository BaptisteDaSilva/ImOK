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

public class MainActivity extends BaseActivity {

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
        MainActivity.this.startActivity(new Intent(MainActivity.this, SendMessageActivity.class));
    }

    public void onClickTelephoner(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, TelephoneActivity.class));
    }

    public void onClickWifi(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, WiFiActivity.class));
    }

    public void onClickGPS(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, GPSActivity.class));
    }

    public void onClickBD(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, BDActivity.class));
    }

    public void onClickProgressBar(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, ProgressBarActivity.class));
    }

    public void onClickProgressBarAsync(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, ProgressBarAsyncActivity.class));
    }

    public void onClickFragment(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, FragmentActivity.class));
    }

    public void onClickNotification(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, NotificationActivity.class));
    }

    public void onClickMaps(View view) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, MapsActivity.class));
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
        MainActivity.this.startActivity(new Intent(MainActivity.this, CameraActivity.class));
    }
}
