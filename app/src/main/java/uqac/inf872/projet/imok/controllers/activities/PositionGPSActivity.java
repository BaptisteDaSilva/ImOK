package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.PositionGPSFragment;

public class PositionGPSActivity extends BaseActivity {

    public static final String BUNDLE_KEY_POSITION_GPS_ID = "BUNDLE_KEY_POSITION_GPS_ID";

    // FOR DATA
    private PositionGPSFragment mPositionGPSFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureAndShowDetailFragment();
    }

//    @Override
//    protected void setDataBinding(ViewDataBinding mDataBinding) {
//
//    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_position;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowDetailFragment() {
        mPositionGPSFragment = (PositionGPSFragment) getSupportFragmentManager().findFragmentById(R.id.activity_position_frame_layout);

        if ( mPositionGPSFragment == null ) {
            mPositionGPSFragment = new PositionGPSFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_position_frame_layout, mPositionGPSFragment)
                    .commit();
        }
    }
}
