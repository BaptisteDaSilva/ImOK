package uqac.inf872.projet.imok;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        MainActivity.this.startActivity(new Intent(MainActivity.this, CameraActivity.class));
    }
}
