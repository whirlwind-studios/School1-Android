package com.whirlwind.school1.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences configuration;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isValid())
                Popup.showIdles(BaseActivity.this);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration= PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(null).registerReceiver(receiver, new IntentFilter(Popup.ACTION_FILTER));
        Popup.showIdles(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(null).unregisterReceiver(receiver);
        Popup.dismissActives();
        super.onPause();
    }

    public boolean isValid() {
        return !isFinishing() && !isDestroyed();
    }
}