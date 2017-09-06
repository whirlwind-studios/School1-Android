package com.whirlwind.school1.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.whirlwind.school1.BuildConfig;
import com.whirlwind.school1.R;

public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);

        Preference market = findPreference("market");
        market.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whirlwind.school1")));
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whirlwind.school1")));
                }
                return true;
            }
        });

        Preference versionName = findPreference("versionName"),
                versionCode = findPreference("versionCode");
        versionName.setSummary(BuildConfig.VERSION_NAME);
        versionCode.setSummary(String.valueOf(BuildConfig.VERSION_CODE));
    }
}
