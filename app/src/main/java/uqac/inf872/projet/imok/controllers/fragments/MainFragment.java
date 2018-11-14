package uqac.inf872.projet.imok.controllers.fragments;


import android.content.Context;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseFragment;

public class MainFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.fragment_main_button_happy)
    Button fragmentMainButtonHappy;

    @BindView(R.id.fragment_main_button_sad)
    Button fragmentMainButtonSad;

    @BindView(R.id.fragment_main_button_horrible)
    Button fragmentMainButtonHorrible;

    // Declare callback
    private OnButtonClickedListener mCallback;

    @Override
    protected MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void configureDesign() {
        // Set onClickListener to buttons
        fragmentMainButtonHappy.setOnClickListener(this);
        fragmentMainButtonSad.setOnClickListener(this);
        fragmentMainButtonHorrible.setOnClickListener(this);
    }

    @Override
    protected void updateDesign() {
    }

    // --------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Call the method that creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    @Override
    public void onClick(View v) {
        // Spread the click to the parent activity
        mCallback.onButtonClicked(v);
    }

    // --------------
    // ACTIONS
    // --------------

    // Create callback to parent activity
    private void createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " must implement OnButtonClickedListener");
        }
    }

    // --------------
    // FRAGMENT SUPPORT
    // --------------

    // Declare our interface that will be implemented by any container activity
    public interface OnButtonClickedListener {
        void onButtonClicked(View view);
    }
}