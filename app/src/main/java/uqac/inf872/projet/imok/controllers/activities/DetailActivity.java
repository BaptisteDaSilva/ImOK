package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.DetailFragment;

public class DetailActivity extends BaseActivity {

    public static final String BUNDLE_KEY_PROJECT_ID = "BUNDLE_KEY_PROJECT_ID";
    public static final String BUNDLE_KEY_PROJECT_IMAGE_URL = "BUNDLE_KEY_PROJECT_IMAGE_URL";
    // FOR DATA
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureAndShowDetailFragment();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_detail;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowDetailFragment() {

        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.activity_detail_frame_layout);

        if ( detailFragment == null ) {
            detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_detail_frame_layout, detailFragment)
                    .commit();
        }
    }
}
