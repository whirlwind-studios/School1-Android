package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
