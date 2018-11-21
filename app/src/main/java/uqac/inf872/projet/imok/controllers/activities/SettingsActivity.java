package uqac.inf872.projet.imok.controllers.activities;

import android.os.Bundle;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseActivity;
import uqac.inf872.projet.imok.controllers.fragments.MySettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new MySettingsFragment())
                .commit();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.activity_settings;
    }
}
