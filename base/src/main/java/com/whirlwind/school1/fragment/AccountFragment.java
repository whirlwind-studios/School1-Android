package com.whirlwind.school1.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.whirlwind.school1.R;

public class AccountFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.account_general);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        findPreference("userName").setSummary(sharedPreferences.getString("userName", "Add a user name"));
        findPreference("schoolName").setSummary(sharedPreferences.getString("schoolName", "Add a school name"));
    }
}
