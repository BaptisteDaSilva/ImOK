package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.PositionWIFIFragment;

public class PositionWIFIActivity extends BaseActivity {

    public static final String BUNDLE_KEY_POSITION_WIFI_ID = "BUNDLE_KEY_POSITION_WIFI_ID";

    // FOR DATA
    private PositionWIFIFragment mPositionWIFIFragment;

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
        mPositionWIFIFragment = (PositionWIFIFragment) getSupportFragmentManager().findFragmentById(R.id.activity_position_frame_layout);

        if ( mPositionWIFIFragment == null ) {
            mPositionWIFIFragment = new PositionWIFIFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_position_frame_layout, mPositionWIFIFragment)
                    .commit();
        }
    }
}
