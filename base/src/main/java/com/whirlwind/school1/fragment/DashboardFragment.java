package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.whirlwind.school1.R;
import com.whirlwind.school1.activity.ConfigItemActivity;
import com.whirlwind.school1.activity.LessonActivity;
import com.whirlwind.school1.activity.MainActivity;
import com.whirlwind.school1.adapter.DashboardAdapter;

public class DashboardFragment extends Fragment implements MainActivity.FloatingActionButtonHandler {

    private RecyclerView recyclerView;

    @Override
    public void handleFloatingActionButton(FloatingActionButton floatingActionButton) {
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ConfigItemActivity.class).putExtra("isNew", true));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = view.findViewById(R.id.fragment_dashboard_recycler_view_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new DashboardAdapter(getActivity()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_timetable)
            startActivity(new Intent(getActivity(), LessonActivity.class));
        else return super.onOptionsItemSelected(item);
        return true;
    }
}
