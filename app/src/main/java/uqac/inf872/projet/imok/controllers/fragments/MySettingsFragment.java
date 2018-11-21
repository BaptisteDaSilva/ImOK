package uqac.inf872.projet.imok.controllers.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import uqac.inf872.projet.imok.R;

public class MySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

//        SwitchPreferenceCompat preference = findPreference("notifications");
        // do something with this preference
    }
}
