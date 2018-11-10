package uqac.inf872.projet.imok;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.perf.metrics.AddTrace;

import uqac.inf872.projet.imok.fragment.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    //Create static variable to identify Intent
    public static final String EXTRA_BUTTON_TAG = "uqac.inf872.projet.imok.DetailActivity.EXTRA_BUTTON_TAG";

    //Declare detail fragment
    private DetailFragment detailFragment;

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Configure and show home fragment
        this.configureAndShowDetailFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Call update method here because we are sure that DetailFragment is visible
        this.updateDetailFragmentTextViewWithIntentTag();
    }

    // --------------
    // FRAGMENTS
    // --------------

    private void configureAndShowDetailFragment() {
        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        if (detailFragment == null) {
            // Create new main fragment
            detailFragment = new DetailFragment();
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }
    }

    // --------------
    // UPDATE UI
    // --------------

    //Update DetailFragment with tag passed from Intent
    private void updateDetailFragmentTextViewWithIntentTag() {
        //Get button's tag from intent
        int buttonTag = getIntent().getIntExtra(EXTRA_BUTTON_TAG, 0);
        //Update DetailFragment's TextView
        detailFragment.updateTextView(buttonTag);
    }
}
