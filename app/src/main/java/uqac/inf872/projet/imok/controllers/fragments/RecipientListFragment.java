package uqac.inf872.projet.imok.controllers.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.RecipientListHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.MenuViewPagerActivity;
import uqac.inf872.projet.imok.controllers.activities.RecipientListActivity;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.Utils;
import uqac.inf872.projet.imok.views.RobotoButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipientListFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.recipient_list_name)
    EditText editTextName;

    @BindView(R.id.recipient_list_destinataires)
    EditText editTextDestinataires;

    @BindView(R.id.recipient_list_btn_delete)
    RobotoButton btnDelete;

    // FOR DATA
    private RecipientList currentRecipientList;

    // -------------------
    // CONFIGURATION
    // -------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_recipient_list;
    }

    @Override
    protected void configureDesign() {

    }

    @Override
    protected void updateDesign() {
        if ( this.getRecipientListIdFromBundle() != null ) {
            this.updateDesignWhenStarting();
        }
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.recipient_list_btn_cancel)
    public void onClickCancel(View view) {
        openMenu();
    }

    @OnClick(R.id.recipient_list_btn_save)
    public void onClickSave(View view) {

        if ( currentRecipientList != null ) {
            currentRecipientList.setName(editTextName.getText().toString());
            currentRecipientList.setRecipients(
                    new ArrayList<>(Arrays.asList(editTextDestinataires.getText().toString().split("\n"))));

            RecipientListHelper.updateRecipientList(currentRecipientList)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()))
                    .addOnSuccessListener(aVoid -> openMenu());
        } else {

            String name = editTextName.getText().toString();
            ArrayList<String> destinataires = new ArrayList<>(Arrays.asList(editTextDestinataires.getText().toString().split("\n")));

            String userID = Utils.getCurrentUser().getUid();

            RecipientListHelper.createRecipientList(name, destinataires, userID)
                    .addOnFailureListener(Utils.onFailureListener(view.getContext()))
                    .addOnSuccessListener(aVoid -> openMenu());
        }
    }

    @OnClick(R.id.recipient_list_btn_delete)
    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous supprimer la liste de destinataire ?");
        builder.setCancelable(false);

        builder.setPositiveButton("OUI", (dialog, id) ->
        {
            RecipientListHelper.deleteRecipientList(currentRecipientList.getIdList());
            openMenu();
        });
        builder.setNegativeButton("NON", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesignWhenStarting() {

        RecipientListHelper.getOKCardWithThisRecipientList(this.getRecipientListIdFromBundle())
                .addOnCompleteListener(command -> {
                    if ( command.getResult().isEmpty() ) {
                        btnDelete.setVisibility(View.VISIBLE);
                    }
                });

        RecipientListHelper.getRecipientList(this.getRecipientListIdFromBundle())
                .addOnSuccessListener(documentSnapshot ->
                {
                    currentRecipientList = documentSnapshot.toObject(RecipientList.class);

                    editTextName.setText(currentRecipientList.getName());

                    StringBuilder sb = new StringBuilder();

                    for (String s : currentRecipientList.getRecipients()) {
                        sb.append(s).append("\n");
                    }

                    editTextDestinataires.setText(sb.toString());
                });
    }

    // -------------------
    // UTILS
    // -------------------

    private void openMenu() {
        Intent intent = new Intent(this.getContext(), MenuViewPagerActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(MenuViewPagerActivity.BUNDLE_KEY_MENU_ID, 1);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private String getRecipientListIdFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(RecipientListActivity.BUNDLE_KEY_RECIPIENT_LIST_ID);
    }
}
