package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.PositionFragment;

public class PositionActivity extends BaseActivity {

    public static final String BUNDLE_KEY_POSITION_ID = "BUNDLE_KEY_OKCARD_ID";
    public static final String BUNDLE_KEY_POSITION_IMAGE_URL = "BUNDLE_KEY_OKCARD_IMAGE_URL";

    // FOR DATA
    private PositionFragment positionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureAndShowDetailFragment();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_position;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowDetailFragment() {
// TODO chnager
        positionFragment = (PositionFragment) getSupportFragmentManager().findFragmentById(R.id.activity_position_frame_layout);

        if ( positionFragment == null ) {
            positionFragment = new PositionFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_position_frame_layout, positionFragment)
                    .commit();
        }
    }
}
