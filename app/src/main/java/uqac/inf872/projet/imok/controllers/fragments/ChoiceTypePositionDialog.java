package uqac.inf872.projet.imok.controllers.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import uqac.inf872.projet.imok.R;

public class ChoiceTypePositionDialog extends DialogFragment {

    public ChoiceTypePositionDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choice_type_position_dialog, container, false);

        ButterKnife.bind(this, view);

        getDialog().setTitle("Nouvelle position");

        return view;
    }

    @OnClick(R.id.dialog_choice_type_btn_gps)
    public void onClick_btnGPS() {
        ChoiceTypePositionListener activity = (ChoiceTypePositionListener) getActivity();
        activity.createNewGPS();

        this.dismiss();
    }

    @OnClick(R.id.dialog_choice_type_btn_wifi)
    public void onClick_btnWIFI() {
        ChoiceTypePositionListener activity = (ChoiceTypePositionListener) getActivity();
        activity.createNewWIFI();

        this.dismiss();
    }

    public interface ChoiceTypePositionListener {
        void createNewGPS();

        void createNewWIFI();
    }
}
