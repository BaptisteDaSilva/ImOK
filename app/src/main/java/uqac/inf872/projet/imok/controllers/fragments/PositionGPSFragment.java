package uqac.inf872.projet.imok.controllers.fragments;

import android.app.AlertDialog;
import android.content.Intent;
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

    // FOR DATA
    private Position currentPosition;

    // -------------------
    // CONFIGURATION
    // -------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_position_gps;
    }

    @Override
    protected void configureDesign() {
    }

    @Override
    protected void updateDesign() {
        if ( this.getPositionIdFromBundle() != null ) {
            this.updateDesignWhenStarting();
        }
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.psoition_gps_btn_cancel)
    public void onClickCancel() {
        Utils.openMenu(this.getContext(), Utils.Menu.Position);
    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

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
            String userID = Utils.getCurrentUser().getUid();

            PositionHelper.createPositionGPS(name, latitude, longitude, rayon, userID)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        }
        Utils.openMenu(this.getContext(), Utils.Menu.Position);
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
