package uqac.inf872.projet.imok.controllers.fragments;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.utils.Preference;

public class MySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference p = Preference.getInstance(this.getContext());

        setPreference(p);

        p.apply();
    }

    private void setPreference(Preference p) {

        SwitchPreference switch_preference = (SwitchPreference) findPreference("switch_preference");

        p.putBoolean(R.string.switch_preference, switch_preference.isChecked());

        // TODO écrire la suite
    }
}
