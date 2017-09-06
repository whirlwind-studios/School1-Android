package com.whirlwind.school1.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.whirlwind.school1.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference("notificationsEnabled").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                /*if ((Boolean) newValue)
                    School1.startServices(getActivity());
                else
                    School1.cancelServices(getActivity());*/
                return true;
            }
        });
        findPreference("showNotifications").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //School1.showNotifications(getActivity());
                return true;
            }
        });
    }
}