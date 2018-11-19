package uqac.inf872.projet.imok.controllers.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.MenuViewPagerActivity;
import uqac.inf872.projet.imok.controllers.activities.PositionActivity;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.views.RobotoButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class PositionFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.position_image)
    ImageView imageView;

    @BindView(R.id.position_name)
    EditText editTextName;

    @BindView(R.id.position_btn_delete)
    RobotoButton btnDelete;

    // FOR DATA
    private Position currentPosition;

    // -------------------
    // CONFIGURATION
    // -------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_position;
    }

    @Override
    protected void configureDesign() {

    }

    @Override
    protected void updateDesign() {
        if ( this.getPositionIdFromBundle() != null ) {
            this.updateDesignWhenStarting();
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.psoition_btn_cancel)
    public void onClickCancel(View view) {
        openMenu();
    }

    @OnClick(R.id.position_btn_save)
    public void onClickSave(View view) {

        if ( currentPosition != null ) {
            currentPosition.setName(editTextName.getText().toString());

//        if (currentPosition.isWifi())
//            currentPosition.setSsid();
//        else{
//            currentPosition.setCoordonnees();
//            currentPosition.setRayon();
//        }

            PositionHelper.updatePosition(currentPosition)
                    .addOnFailureListener(this.onFailureListener())
                    .addOnSuccessListener(aVoid -> openMenu());
        } else {

            String name = editTextName.getText().toString();

            // TODO changer
            boolean isWifi = true;

            String SSID = "";

            double longitude = 0.0;
            double latitude = 0.0;
            int rayon = 30;

            String userID = "";

            if ( isWifi ) {
                PositionHelper.createPositionWifi(name, SSID, userID)
                        .addOnFailureListener(this.onFailureListener())
                        .addOnSuccessListener(aVoid -> openMenu());
            } else {
                PositionHelper.createPositionGPS(name, longitude, latitude, rayon, userID)
                        .addOnFailureListener(this.onFailureListener())
                        .addOnSuccessListener(aVoid -> openMenu());
            }
        }
    }


    @OnClick(R.id.position_btn_delete)
    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous supprimer la position ?");
        builder.setCancelable(false);

        builder.setPositiveButton("OUI", (dialog, id) ->
        {
            PositionHelper.deletePosition(currentPosition.getId());
            openMenu();
        });
        builder.setNegativeButton("NON", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesignWhenStarting() {

        PositionHelper.getPosition(this.getPositionIdFromBundle())
                .addOnSuccessListener(documentSnapshot ->
                {
                    currentPosition = documentSnapshot.toObject(Position.class);

                    if ( currentPosition.isWifi() ) {
                        Glide.with(this).asDrawable().load(R.drawable.ic_wifi_white_large).into(imageView);
                    } else {
                        Glide.with(this).asDrawable().load(R.drawable.ic_location_white_large).into(imageView);
                    }

                    editTextName.setText(currentPosition.getName());
                });
    }

    // -------------------
    // UTILS
    // -------------------

    private void openMenu() {
        Intent intent = new Intent(this.getContext(), MenuViewPagerActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(MenuViewPagerActivity.BUNDLE_KEY_MENU_ID, 2);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private String getPositionIdFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(PositionActivity.BUNDLE_KEY_POSITION_ID);
    }
}
