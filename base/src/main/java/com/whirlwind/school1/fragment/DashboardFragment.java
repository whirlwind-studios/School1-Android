package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.whirlwind.school1.R;
import com.whirlwind.school1.activity.MainActivity;
import com.whirlwind.school1.adapter.DashboardAdapter;

public class DashboardFragment extends Fragment implements MainActivity.FloatingActionButtonHandler {

    private final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(null);
    private RecyclerView recyclerView;
    private final SharedPreferences.OnSharedPreferenceChangeListener hideCompletedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            /*if (key.equals("hideCompleted")) {
                if (sharedPreferences.getBoolean("hideCompleted", true))
                    ((DashboardAdapter) recyclerView.getAdapter()).hideCompleted();
                else
                    ((DashboardAdapter) recyclerView.getAdapter()).rebuild(recyclerView);
            }*/
        }
    };

    @Override
    public void handleFloatingActionButton(FloatingActionButton floatingActionButton) {
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), ConfigEventActivity.class).putExtra("isNew", true));
                //ConfigEventSheet fragment = new ConfigEventSheet();
                //fragment.show(((BaseActivity) getActivity()).getSupportFragmentManager(), "Some fragment");
                //startActivity(new Intent(getActivity(), TestActivity.class));
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
        recyclerView.setAdapter(new DashboardAdapter());
        //dataInterface.getConfiguration().registerOnSharedPreferenceChangeListener(hideCompletedListener);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_timetable) {
            //new TimetablePopup().show(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        //dataInterface.getConfiguration().unregisterOnSharedPreferenceChangeListener(hideCompletedListener);
        super.onDestroy();
    }
}
