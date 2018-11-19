package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.RecipientListFragment;

public class RecipientListActivity extends BaseActivity {

    public static final String BUNDLE_KEY_RECIPIENT_LIST_ID = "BUNDLE_KEY_RECIPIENT_LIST_ID";

    // FOR DATA
    private RecipientListFragment recipientListFragment;

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
        return R.layout.activity_recipient_list;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowDetailFragment() {

        recipientListFragment = (RecipientListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_recipient_list_frame_layout);

        if ( recipientListFragment == null ) {
            recipientListFragment = new RecipientListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_recipient_list_frame_layout, recipientListFragment)
                    .commit();
        }
    }
}
