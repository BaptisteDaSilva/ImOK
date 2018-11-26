package uqac.inf872.projet.imok.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.adapters.PageAdapter;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.ChoiceTypePositionDialog;
import uqac.inf872.projet.imok.controllers.fragments.ListOKCardFragment;
import uqac.inf872.projet.imok.controllers.fragments.ListPositionFragment;
import uqac.inf872.projet.imok.controllers.fragments.ListRecipientsListFragment;

public class MenuViewPagerActivity extends BaseActivity implements ChoiceTypePositionDialog.ChoiceTypePositionListener {

    public static final String BUNDLE_KEY_MENU_ID = "BUNDLE_KEY_MENU_ID";

    //    private static final String[] TITLE = new String[] {"OK Card", "Destinataire", "Position"};
    private static final int[] TITLE_ICON = new int[]{R.drawable.ic_ok, R.drawable.ic_list_alt_white, R.drawable.ic_location_white};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureViewPagerAndTabs();
    }

    private void configureViewPagerAndTabs() {
        // Get ViewPager from layout
        ViewPager pager = findViewById(R.id.activity_menu_wiew_pager);
        // Set Adapter PageAdapter and glue it together
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFrag(ListOKCardFragment.newInstance()); //, TITLE[0]);
        adapter.addFrag(ListRecipientsListFragment.newInstance()); //, TITLE[1]);
        adapter.addFrag(ListPositionFragment.newInstance()); //, TITLE[2]);
        pager.setAdapter(adapter);

        // Get TabLayout from layout
        TabLayout tabs = findViewById(R.id.activity_menu_wiew_pager_tabs);
        // Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager);
        // Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);

        tabs.getTabAt(0).setIcon(TITLE_ICON[0]);
        tabs.getTabAt(1).setIcon(TITLE_ICON[1]);
        tabs.getTabAt(2).setIcon(TITLE_ICON[2]);

        Bundle bundle = getIntent().getExtras();

        if ( bundle != null ) {
            int idMenu = bundle.getInt(MenuViewPagerActivity.BUNDLE_KEY_MENU_ID);

            TabLayout.Tab tab = tabs.getTabAt(idMenu);
            tab.select();
        }

        // TODO https://developer.android.com/training/implementing-navigation/temporal
    }

//    @Override
//    protected void setDataBinding(ViewDataBinding mDataBinding) {
//
//    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.activity_menu_view_pager;
    }

    // -------------------
    // NAVIGATION
    // -------------------

    public void createNewGPS() {
        Intent intent = new Intent(this, PositionGPSActivity.class);
        startActivity(intent);
    }

    public void createNewWIFI() {
        Intent intent = new Intent(this, PositionWIFIActivity.class);
        startActivity(intent);
    }
}
