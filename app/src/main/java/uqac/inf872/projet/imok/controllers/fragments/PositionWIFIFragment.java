package uqac.inf872.projet.imok.controllers.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.PositionWIFIActivity;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.utils.Utils;
import uqac.inf872.projet.imok.views.RobotoButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class PositionWIFIFragment extends BaseFragment {


    // FOR DESIGN
    @BindView(R.id.position_wifi_name)
    EditText editTextName;

//    @BindView(R.id.position_wifi_list)
//    Spinner spinnerListWifi;

    @BindView(R.id.position_wifi_list)
    ListView listViewWifi;

    @BindView(R.id.position_wifi_btn_delete)
    RobotoButton btnDelete;

    // FOR DATA
    private Position currentPosition;

    private ArrayList<String> listWifi;

    // -------------------
    // CONFIGURATION
    // -------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_position_wifi;
    }

    @Override
    protected void configureDesign() {
        if ( Utils.isGrantedPermission(this, Utils.Permission.ACCESS_WIFI_STATE) ) {
            setListConfiguredNetworks();
        }
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

    @OnClick(R.id.psoition_btn_cancel)
    public void onClickCancel(View view) {
        Utils.openMenu(this.getContext(), Utils.Menu.Position);
    }

    @OnClick(R.id.position_btn_save)
    public void onClickSave(View view) {

        if ( currentPosition != null ) {
            currentPosition.setName(editTextName.getText().toString());

//            currentPosition.setSsid((String) spinnerListWifi.getSelectedItem());

            List<String> ssid = new ArrayList<>();

            SparseBooleanArray checked = listViewWifi.getCheckedItemPositions();
            for (int i = 0; i < listViewWifi.getCount(); i++) {
                if ( checked.get(i) ) {
                    ssid.add(listWifi.get(i));
                }
            }

            currentPosition.setSsid(ssid);

            ArrayList idSelected = new ArrayList();

            for (long l : listViewWifi.getCheckedItemIds()) {
                idSelected.add(l);
            }

            for (int pos = 0; pos < listViewWifi.getAdapter().getCount(); pos++) {

                if ( idSelected.contains(listViewWifi.getAdapter().getItemId(pos)) ) {

                }

                String s = (String) listViewWifi.getAdapter().getItem(pos);

                if ( s.equals(currentPosition.getSsid()) ) {
                    listViewWifi.setItemChecked(pos, true);
                }
            }

            PositionHelper.updatePosition(currentPosition)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        } else {

            String name = editTextName.getText().toString();

//            String ssid = (String) spinnerListWifi.getSelectedItem();

            List<String> ssid = new ArrayList<>();

            SparseBooleanArray checked = listViewWifi.getCheckedItemPositions();
            for (int i = 0; i < listViewWifi.getCount(); i++) {
                if ( checked.get(i) ) {
                    ssid.add(listWifi.get(i));
                }
            }

            String userID = Utils.getCurrentUser().getUid();

            PositionHelper.createPositionWifi(name, ssid, userID)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        }
        Utils.openMenu(this.getContext(), Utils.Menu.Position);
    }


    @OnClick(R.id.position_wifi_btn_delete)
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

//                        for (int pos = 0; pos < spinnerListWifi.getAdapter().getCount(); pos++) {
//                            String s = (String) spinnerListWifi.getAdapter().getItem(pos);
//
//                            if ( s.equals(currentPosition.getSsid()) ) {
//                                spinnerListWifi.setSelection(pos);
//                                return;
//                            }
//                        }

                        if ( currentPosition.getSsid() != null ) {
                            for (int pos = 0; pos < listViewWifi.getAdapter().getCount(); pos++) {
                                String s = (String) listViewWifi.getAdapter().getItem(pos);

                                if ( currentPosition.getSsid().contains(s) ) {
                                    listViewWifi.setItemChecked(pos, true);
                                }
                            }
                        }
                    }
                });
    }

    // -------------------
    // UTILS
    // -------------------

    private String getPositionIdFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(PositionWIFIActivity.BUNDLE_KEY_POSITION_WIFI_ID);
    }

    @AfterPermissionGranted(Utils.PERMISSION_ACCESS_WIFI_STATE_RC)
    private void setListConfiguredNetworks() {
        WifiManager wifiManager = (WifiManager) this.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        listWifi = new ArrayList<>();

        for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
            listWifi.add(wc.SSID.replace("\"", ""));
        }

        Collections.sort(listWifi);

//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, listWifi);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spinnerListWifi.setAdapter(adapter);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_multiple_choice, listWifi);
        listViewWifi.setAdapter(arrayAdapter);
    }
}
