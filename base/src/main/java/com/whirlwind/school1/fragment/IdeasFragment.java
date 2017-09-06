package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whirlwind.school1.R;

import com.whirlwind.school1.activity.MainActivity;

public class IdeasFragment extends Fragment implements MainActivity.TabLayoutHandler {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ideas, container, false);

        return view;
    }

    @Override
    public void handleTabLayout(TabLayout tabLayout) {
        tabLayout.setVisibility(View.VISIBLE);
    }
}
