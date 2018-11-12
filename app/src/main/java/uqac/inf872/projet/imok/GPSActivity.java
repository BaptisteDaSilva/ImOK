package uqac.inf872.projet.imok;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.perf.metrics.AddTrace;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GPSActivity extends AppCompatActivity {

    private static final String PERMS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_FINE_LOCATION_PERMS = 100;

    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    double longitudeGPS, latitudeGPS;
    double longitudeNetwork, latitudeNetwork;
    TextView longitudeValueBest, latitudeValueBest;

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueBest.setText(longitudeBest + "");
                    latitudeValueBest.setText(latitudeBest + "");
                    Toast.makeText(GPSActivity.this, "Best Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    TextView longitudeValueGPS, latitudeValueGPS;
    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                    Toast.makeText(GPSActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    TextView longitudeValueNetwork, latitudeValueNetwork;
    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueNetwork.setText(longitudeNetwork + "");
                    latitudeValueNetwork.setText(latitudeNetwork + "");
                    Toast.makeText(GPSActivity.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        longitudeValueBest = findViewById(R.id.longitudeValueBest);
        latitudeValueBest = findViewById(R.id.latitudeValueBest);
        longitudeValueGPS = findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = findViewById(R.id.latitudeValueGPS);
        longitudeValueNetwork = findViewById(R.id.longitudeValueNetwork);
        latitudeValueNetwork = findViewById(R.id.latitudeValueNetwork);
    }

    private boolean checkLocation() {
        if ( !isLocationEnabled() )
            showAlert();

        return !isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_FINE_LOCATION_PERMS)
    public void toggleGPSUpdates(View view) {
        if ( checkLocation() )
            return;
        Button button = (Button) view;
        if ( button.getText().equals(getResources().getString(R.string.pause)) ) {
            locationManager.removeUpdates(locationListenerGPS);
            button.setText(R.string.resume);
        } else {
            if ( !EasyPermissions.hasPermissions(this, PERMS_FINE_LOCATION) ) {
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_fine_location), RC_FINE_LOCATION_PERMS, PERMS_FINE_LOCATION);
                return;
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);
            button.setText(R.string.pause);
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_FINE_LOCATION_PERMS)
    public void toggleBestUpdates(View view) {
        if ( checkLocation() )
            return;
        Button button = (Button) view;
        if ( button.getText().equals(getResources().getString(R.string.pause)) ) {
            locationManager.removeUpdates(locationListenerBest);
            button.setText(R.string.resume);
        } else {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if ( provider != null ) {
                if ( !EasyPermissions.hasPermissions(this, PERMS_FINE_LOCATION) ) {
                    EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_fine_location), RC_FINE_LOCATION_PERMS, PERMS_FINE_LOCATION);
                    return;
                }

                locationManager.requestLocationUpdates(provider, 2 * 60 * 1000, 10, locationListenerBest);
                button.setText(R.string.pause);
                Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_FINE_LOCATION_PERMS)
    public void toggleNetworkUpdates(View view) {
        if ( checkLocation() )
            return;
        Button button = (Button) view;
        if ( button.getText().equals(getResources().getString(R.string.pause)) ) {
            locationManager.removeUpdates(locationListenerNetwork);
            button.setText(R.string.resume);
        } else {

            if ( !EasyPermissions.hasPermissions(this, PERMS_FINE_LOCATION) ) {
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_fine_location), RC_FINE_LOCATION_PERMS, PERMS_FINE_LOCATION);
                return;
            }

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 60 * 1000, 10, locationListenerNetwork);
            Toast.makeText(this, "Network provider started running", Toast.LENGTH_LONG).show();
            button.setText(R.string.pause);
        }
    }

}