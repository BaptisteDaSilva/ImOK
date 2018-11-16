package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomNavigationView;
import android.support.test.espresso.idling.CountingIdlingResource;

import butterknife.BindView;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.MenuFragment;

public class MenuActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.activity_menu_bottom_navigation)
    BottomNavigationView bottomNavigationView;

    // FOR DATA
    private MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureAndShowMainFragment();
        this.configureBottomView();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_menu;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowMainFragment() {

        menuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.activity_menu_frame_layout);

        if ( menuFragment == null ) {
            menuFragment = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_menu_frame_layout, menuFragment)
                    .commit();
        }
    }

    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

    // -------------------
    // UI
    // -------------------

    private Boolean updateMainFragment(Integer integer) {
        switch (integer) {
            case R.id.action_android:
                this.menuFragment.updateDesignWhenUserClickedBottomView(MenuFragment.REQUEST_ANDROID);
                break;
            case R.id.action_logo:
                this.menuFragment.updateDesignWhenUserClickedBottomView(MenuFragment.REQUEST_LOGO);
                break;
            case R.id.action_landscape:
                this.menuFragment.updateDesignWhenUserClickedBottomView(MenuFragment.REQUEST_LANDSCAPE);
                break;
        }
        return true;
    }

    // -------------------
    // TEST
    // -------------------

    @VisibleForTesting
    public CountingIdlingResource getEspressoIdlingResourceForMainFragment() {
        return this.menuFragment.getEspressoIdlingResource();
    }
}
