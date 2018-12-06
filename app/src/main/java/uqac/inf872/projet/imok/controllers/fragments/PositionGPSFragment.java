package uqac.inf872.projet.imok.controllers.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.firestore.GeoPoint;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.MapsActivity;
import uqac.inf872.projet.imok.controllers.activities.PositionGPSActivity;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.utils.Utils;
import uqac.inf872.projet.imok.views.RobotoButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PositionGPSFragment extends BaseFragment {

    private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire

    // FOR DESIGN
    @BindView(R.id.position_gps_name)
    EditText editTextName;

    @BindView(R.id.position_gps_latitude_value)
    EditText editTextLatitude;

    @BindView(R.id.position_gps_longitude_value)
    EditText editTextLongitude;

    @BindView(R.id.position_gps_rayon_edit)
    EditText editTextRayon;

    @BindView(R.id.position_gps_btn_delete)
    RobotoButton btnDelete;


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    // -------------------
    // CONFIGURATION
    // -------------------
    // FOR DATA
    private String idPosition;
    private Position currentPosition;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_position_gps;
    }

    @Override
    protected void configureDesign() {
    }

    // -------------------
    // ACTIONS
    // -------------------

    @Override
    protected void updateDesign() {
        if ( this.getPositionIdFromBundle() != null ) {
            this.updateDesignWhenStarting();
        }
    }

    @OnClick(R.id.psoition_gps_btn_cancel)
    public void onClickCancel() {
        Utils.openMenu(this.getContext(), Utils.Menu.Position);
    }

    @OnClick(R.id.position_gps_btn_save)
    public void onClickSave(View view) {

        String name = editTextName.getText().toString();

        double latitude = Double.valueOf(editTextLatitude.getText().toString());
        double longitude = Double.valueOf(editTextLongitude.getText().toString());

        int rayon = Integer.valueOf(editTextRayon.getText().toString());

        if ( currentPosition != null ) {
            currentPosition.setName(name);

            currentPosition.setRayon(rayon);

            currentPosition.setCoordonnees(new GeoPoint(latitude, longitude));

            PositionHelper.updatePosition(currentPosition)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        } else {

            idPosition = PositionHelper.createPosition();

            String userID = Utils.getCurrentUser().getUid();

            PositionHelper.createPositionGPS(idPosition, name, latitude, longitude, rayon, userID)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        }

        Utils.addReceiverForProximityAlert(this.getContext());

        if ( Utils.isGrantedPermission(this, Utils.Permission.ACCESS_FINE_LOCATION) ) {


            addProximityAlert();
        }

        Utils.openMenu(this.getContext(), Utils.Menu.Position);
    }

    // TODO marche pas si recupère de la bd en ligne
    @AfterPermissionGranted(Utils.PERMISSION_ACCESS_FINE_LOCATION_RC)
    private void addProximityAlert() {
        String name = editTextName.getText().toString();

        double latitude = Double.valueOf(editTextLatitude.getText().toString());
        double longitude = Double.valueOf(editTextLongitude.getText().toString());

        int rayon = Integer.valueOf(editTextRayon.getText().toString());

        addProximityAlert(idPosition, name, latitude, longitude, rayon);
    }

    @SuppressLint("MissingPermission")
    public void addProximityAlert(String idPosition, String nom, double latitude, double longitude, int rayon) {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent(Utils.PROX_ALERT_INTENT);
        intent.putExtra(Utils.PROX_ALERT_INTENT_EXTRA_ID, idPosition);
        intent.putExtra(Utils.PROX_ALERT_INTENT_EXTRA_NAME, nom);

        // TODO changer id pour que sa marche
        PendingIntent proximityIntent = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                rayon, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
    }

    @OnClick(R.id.position_gps_btn_choose_adresse)
    public void onClickChooseAdress(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE ) {
            if ( resultCode == RESULT_OK ) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);

                Log.i(Utils.TAG, "Place: " + place.getName());

                editTextLatitude.setText(String.valueOf(place.getLatLng().latitude));
                editTextLongitude.setText(String.valueOf(place.getLatLng().longitude));
            } else if ( resultCode == PlaceAutocomplete.RESULT_ERROR ) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);

                // TODO: Handle the error.
                Log.i(Utils.TAG, status.getStatusMessage());

            } else if ( resultCode == RESULT_CANCELED ) {
                // The user canceled the operation.
            }
        }
    }

    @OnClick(R.id.position_gps_btn_open_map)
    public void onClickOpenMap(View view) {
        Intent intent = new Intent(getContext(), MapsActivity.class);

        intent.putExtra("rayon", editTextRayon.getText().toString());
        intent.putExtra("latitude", editTextLatitude.getText().toString());
        intent.putExtra("longitude", editTextLongitude.getText().toString());

        startActivityForResult(intent, 10);
    }

    @OnClick(R.id.position_gps_btn_delete)
    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous supprimer la position ?");
        builder.setCancelable(false);

        builder.setPositiveButton("OUI", (dialog, id) ->
        {
            PositionHelper.deletePosition(currentPosition.getId());
            Utils.openMenu(this.getContext(), Utils.Menu.Position);

            PositionHelper.getPositionGPS().get().addOnSuccessListener(task -> {
                if ( task.isEmpty() ) {
                    Utils.unregisterReceiverForProximityAlert(this.getContext());
                }
            });
        });
        builder.setNegativeButton("NON", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesignWhenStarting() {

        PositionHelper.getOKCardWithThisTrigger(this.getPositionIdFromBundle())
                .addOnCompleteListener(command -> {
                    if ( command.getResult() == null || command.getResult().isEmpty() ) {
                        btnDelete.setVisibility(View.VISIBLE);
                    }
                });

        PositionHelper.getPosition(this.getPositionIdFromBundle())
                .addOnSuccessListener(documentSnapshot ->
                {
                    currentPosition = documentSnapshot.toObject(Position.class);

                    if ( currentPosition != null ) {
                        idPosition = currentPosition.getId();

                        editTextName.setText(currentPosition.getName());
                        editTextRayon.setText(String.valueOf(currentPosition.getRayon()));
                        editTextLatitude.setText(String.valueOf(currentPosition.getCoordonnees().getLatitude()));
                        editTextLongitude.setText(String.valueOf(currentPosition.getCoordonnees().getLongitude()));
                    }
                });
    }

    // -------------------
    // UTILS
    // -------------------

    private String getPositionIdFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(PositionGPSActivity.BUNDLE_KEY_POSITION_GPS_ID);
    }
}
