package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.OKCardFragment;

public class OKCardActivity extends BaseActivity {

    public static final String BUNDLE_KEY_OK_CARD_ID = "BUNDLE_KEY_OKCARD_ID";
    public static final String BUNDLE_KEY_OK_CARD_IMAGE_URL = "BUNDLE_KEY_OKCARD_IMAGE_URL";

    // FOR DATA
    private OKCardFragment OKCardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureAndShowDetailFragment();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_ok_card;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowDetailFragment() {

        OKCardFragment = (OKCardFragment) getSupportFragmentManager().findFragmentById(R.id.activity_ok_card_frame_layout);

        if ( OKCardFragment == null ) {
            OKCardFragment = new OKCardFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_ok_card_frame_layout, OKCardFragment)
                    .commit();
        }
    }
}
