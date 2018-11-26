package uqac.inf872.projet.imok.controllers.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.api.RecipientListHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.Utils;

public class OKCardFragment extends BaseFragment {

    private static final String PERMS_SEND_SMS = Manifest.permission.SEND_SMS;
    private static final int RC_FINE_SEND_SMS_PERMS = 100;

    // FOR DESIGN
    @BindView(R.id.ok_card_image)
    ImageView imageView;

    @BindView(R.id.ok_card_name)
    EditText editTextName;

    @BindView(R.id.ok_card_message)
    EditText editTextMessage;

    @BindView(R.id.ok_card_recipient_list)
    Spinner spinnerRecipientList;

    @BindView(R.id.ok_card_switch_gps)
    Switch switchGPS;

    @BindView(R.id.ok_card_list_gps)
    Spinner spinnerGPS;

    @BindView(R.id.ok_card_switch_wifi)
    Switch switchWifi;

    @BindView(R.id.ok_card_list_wifi)
    Spinner spinnerWifi;

    @BindView(R.id.ok_card_btn_botoom)
    LinearLayout linearLayoutBtnBottom;

    // FOR DATA
    private OKCard currentOKCard;

    // -------------------
    // CONFIGURATION
    // -------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_ok_card;
    }

    @Override
    protected void configureDesign() {
        spinnerGPS.setEnabled(false);
        spinnerWifi.setEnabled(false);
    }

    @Override
    protected void updateDesign() {
        Query queryRecipientList = RecipientListHelper.getRecipientList();

        if ( queryRecipientList != null ) {
            queryRecipientList.get().addOnCompleteListener(task -> {
                if ( task.isSuccessful() ) {
                    ArrayList<RecipientList> list = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.toObject(RecipientList.class));
                    }

                    ArrayAdapter<RecipientList> adapter =
                            new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerRecipientList.setAdapter(adapter);
                } else {
                    Utils.onFailureListener(getContext(), task.getException());
                }
            });
        }

        Query queryPositionGPS = PositionHelper.getPositionGPS();

        if ( queryPositionGPS != null ) {
            queryPositionGPS.get().addOnCompleteListener(task -> {
                if ( task.isSuccessful() ) {
                    ArrayList<Position> list = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.toObject(Position.class));
                    }

                    ArrayAdapter<Position> adapter =
                            new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerGPS.setAdapter(adapter);
                } else {
                    Utils.onFailureListener(getContext(), task.getException());
                }
            });
        }

        Query queryPositionWifi = PositionHelper.getPositionWifi();

        if ( queryPositionWifi != null ) {
            queryPositionWifi.get().addOnCompleteListener(task -> {
                if ( task.isSuccessful() ) {
                    ArrayList<Position> list = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.toObject(Position.class));
                    }

                    ArrayAdapter<Position> adapter =
                            new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerWifi.setAdapter(adapter);
                } else {
                    Utils.onFailureListener(getContext(), task.getException());
                }
            });
        }

        if ( this.getOKCardIdFromBundle() != null ) {
            this.updateDesignWhenStarting();
        } else {
            linearLayoutBtnBottom.setVisibility(View.GONE);
        }
    }

    // -------------------
    // ACTIONS
    // -------------------

    // TODO gerer bouton retour
//    public static Dialog cancelOnOKCard(Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        builder.setTitle("Confirmation");
//        builder.setMessage("Enregistrer vos modifications ou annuler ?");
//        builder.setCancelable(true);
//
//        builder.setPositiveButton("ENREGISTRER", (dialog, id) ->
//        {
//            // TODO ecrire sauvegade
//        });
//        builder.setNegativeButton("IGNORER", (dialog, id) -> dialog.cancel());
//        return builder.create();
//    }

    @OnClick(R.id.ok_card_btn_cancel)
    public void onClickCancel(View view) {
        Utils.openMenu(this.getContext(), Utils.Menu.OKCard);
    }

    @OnClick(R.id.ok_card_btn_save)
    public void onClickSave(View view) {
        String name = editTextName.getText().toString();
        String message = editTextMessage.getText().toString();

        // TODO changer
        String urlPicture = "data:image/png;base64,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";

        RecipientList recipientList = (RecipientList) spinnerRecipientList.getSelectedItem();

        List<String> triggers = new ArrayList<>();

        if ( switchGPS.isChecked() ) {
            Position positionGPS = (Position) spinnerGPS.getSelectedItem();
            triggers.add(positionGPS.getId());
        }

        if ( switchWifi.isChecked() ) {
            Position positionWIFI = (Position) spinnerWifi.getSelectedItem();
            triggers.add(positionWIFI.getId());
        }

        if ( currentOKCard != null ) {

            currentOKCard.setName(name);
            currentOKCard.setMessage(message);
//            currentOKCard.setUrlPicture(urlPicture); // TODO gerer
            currentOKCard.setIdListe(recipientList.getId());
            currentOKCard.setIdTrigger(triggers);


            OKCardHelper.updateOKCard(currentOKCard)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        } else {
            OKCardHelper.createOKCard(name, message, urlPicture, recipientList.getId(), triggers, Utils.getCurrentUser().getUid())
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()));
        }

        Utils.openMenu(this.getContext(), Utils.Menu.OKCard);
    }

    @OnClick(R.id.ok_card_btn_envoyer)
    public void onClickEnvoyer(View view) {
        if ( EasyPermissions.hasPermissions(view.getContext(), PERMS_SEND_SMS) ) {
            sendMessage();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_sens_message), RC_FINE_SEND_SMS_PERMS, PERMS_SEND_SMS);
        }
    }

    @OnClick(R.id.ok_card_btn_delete)
    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous supprimer l'OKCard ?");
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.yes, (dialog, id) ->
        {
            OKCardHelper.deleteOKCard(currentOKCard.getId());
            Utils.openMenu(this.getContext(), Utils.Menu.OKCard);
        });
        builder.setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    @OnCheckedChanged(R.id.ok_card_switch_gps)
    public void onCheckedChangedGPS(CompoundButton buttonView, boolean isChecked) {
        if ( isChecked ) {
            spinnerGPS.setEnabled(true);
        } else {
            spinnerGPS.setEnabled(false);
        }
    }

    @OnCheckedChanged(R.id.ok_card_switch_wifi)
    public void onCheckedChangedWifi(CompoundButton buttonView, boolean isChecked) {
        if ( isChecked ) {
            spinnerWifi.setEnabled(true);
        } else {
            spinnerWifi.setEnabled(false);
        }
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesignWhenStarting() {
        Glide.with(this).load(this.getImageURLFromBundle()).into(this.imageView);

        OKCardHelper.getOKCard(this.getOKCardIdFromBundle()).get()
                .addOnSuccessListener(documentSnapshotOKCard ->
                {
                    currentOKCard = documentSnapshotOKCard.toObject(OKCard.class);

                    editTextName.setText(currentOKCard.getName());
                    editTextMessage.setText(currentOKCard.getMessage());

                    RecipientListHelper.getRecipientList(this.currentOKCard.getIdListe())
                            .addOnSuccessListener(documentSnapshotRecipient ->
                            {
                                RecipientList recipientList = documentSnapshotRecipient.toObject(RecipientList.class);

                                for (int pos = 0; pos < spinnerRecipientList.getAdapter().getCount(); pos++) {
                                    RecipientList rl = (RecipientList) spinnerRecipientList.getAdapter().getItem(pos);

                                    if ( rl.getId().equals(recipientList.getId()) ) {
                                        spinnerRecipientList.setSelection(pos);
                                        return;
                                    }
                                }
                            });

                    for (String idPosition : this.currentOKCard.getIdTrigger()) {
                        PositionHelper.getPosition(idPosition)
                                .addOnSuccessListener(documentSnapshotPosition ->
                                {
                                    Position position = documentSnapshotPosition.toObject(Position.class);

                                    if ( position.isWifi() ) {
                                        for (int pos = 0; pos < spinnerWifi.getAdapter().getCount(); pos++) {
                                            Position p = (Position) spinnerWifi.getAdapter().getItem(pos);

                                            if ( p.getId().equals(position.getId()) ) {
                                                switchWifi.setChecked(true);
                                                spinnerWifi.setSelection(pos);
                                                return;
                                            }
                                        }
                                    } else {
                                        for (int pos = 0; pos < spinnerGPS.getAdapter().getCount(); pos++) {
                                            Position p = (Position) spinnerGPS.getAdapter().getItem(pos);

                                            if ( p.getId().equals(position.getId()) ) {
                                                switchGPS.setChecked(true);
                                                spinnerGPS.setSelection(pos);
                                                return;
                                            }
                                        }
                                    }
                                });
                    }

                });
    }

    // -------------------
    // UTILS
    // -------------------

    private String getOKCardIdFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();

        if ( bundle != null ) {
            return bundle.getString(OKCardActivity.BUNDLE_KEY_OK_CARD_ID);
        }

        return null;
    }

    private String getImageURLFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL);
    }

    @AfterPermissionGranted(RC_FINE_SEND_SMS_PERMS)
    public void sendMessage() {

        String msg = editTextMessage.getText().toString();

        RecipientList recipientList = (RecipientList) spinnerRecipientList.getSelectedItem();

        SmsManager manager = SmsManager.getDefault();

        RecipientListHelper.getRecipientList(recipientList.getId()).addOnSuccessListener(documentSnapshot ->
        {
            RecipientList currentRecipientList = documentSnapshot.toObject(RecipientList.class);

            for (String num : currentRecipientList.getRecipients()) {

                manager.sendTextMessage(num, null, msg, null, null); // piSend, piDelivered);
            }
        });

        Toast.makeText(this.getActivity(), "Message envoyé", Toast.LENGTH_SHORT).show();
    }
}
