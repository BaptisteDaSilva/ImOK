package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private final float DEFAULT_ZOOM = 15.0F;
    private GoogleMap mMap;
    private Circle circleMap;
    private int rayon;
    private LatLng defaultLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getIntent().hasExtra("rayon") ) {
            rayon = Integer.parseInt(getIntent().getStringExtra("rayon"));
        } else {
            rayon = 100;// TODO gerer default
        }

        String sLatitude = getIntent().getStringExtra("latitude");
        String sLongitude = getIntent().getStringExtra("longitude");

        double latitude = 0.0;
        double longitude = 0.0;

        if ( !"".equals(sLatitude) ) {
            latitude = Double.parseDouble(sLatitude);
        }

        if ( !"".equals(sLongitude) ) {
            longitude = Double.parseDouble(sLongitude);
        }

        defaultLocation = new LatLng(latitude, longitude);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if ( mapFragment != null ) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.activity_maps;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addCircle(defaultLocation);

    }

    public void addCircle(LatLng coordinate) {
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, DEFAULT_ZOOM);
        mMap.animateCamera(location);

        if ( circleMap != null )
            circleMap.remove();

        circleMap = mMap.addCircle(new CircleOptions()
                .center(coordinate)
                .radius(rayon)
                .strokeWidth(15)
                .strokeColor(R.color.colorPrimary)
                .fillColor(R.color.colorAccentMap));
    }
}
