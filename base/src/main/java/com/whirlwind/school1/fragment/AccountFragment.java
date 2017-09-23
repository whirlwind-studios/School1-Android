package com.whirlwind.school1.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.whirlwind.school1.R;
import com.whirlwind.school1.activity.SigninActivity;

public class AccountFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.account);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //findPreference("userName").setSummary(sharedPreferences.getString("userName", "Add a user name"));
        //findPreference("schoolName").setSummary(sharedPreferences.getString("schoolName", "Add a school name"));

        findPreference("actionChangeUser").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SigninActivity.class));
                return true;
            }
        });
    }
}
