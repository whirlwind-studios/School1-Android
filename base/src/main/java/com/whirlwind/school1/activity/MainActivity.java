package com.whirlwind.school1.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.fragment.AboutFragment;
import com.whirlwind.school1.fragment.AccountFragment;
import com.whirlwind.school1.fragment.CoursesFragment;
import com.whirlwind.school1.fragment.DashboardFragment;
import com.whirlwind.school1.fragment.IdeasFragment;
import com.whirlwind.school1.fragment.SettingsFragment;
import com.whirlwind.school1.fragment.TimetableFragment;

import java.util.Random;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int[] fragmentIds = {
            R.id.action_dashboard, R.id.action_ideas,
            R.id.action_account, R.id.action_courses, R.id.action_timetable,
            R.id.action_settings, R.id.action_about
    };

    private static final Fragment[] navigationFragments = {
            new DashboardFragment(), new IdeasFragment(),
            new AccountFragment(), new CoursesFragment(), new TimetableFragment(),
            new SettingsFragment(), new AboutFragment()
    };

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton floatingActionButton;
    private TabLayout tabLayout;

    private Fragment currentFragment;
    private int drawerItemId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        navigationView = findViewById(R.id.activity_main_navigation_view);
        floatingActionButton = findViewById(R.id.activity_main_floating_action_button);
        tabLayout = findViewById(R.id.activity_main_tab_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.content_description_navigation_drawer_open, R.string.content_description_navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(currentFragment!=null)
                    getFragmentManager().beginTransaction().replace(R.id.activity_main_container, currentFragment).commit();
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.navigation_header_layout_name);
        TextView school = headerView.findViewById(R.id.navigation_header_layout_school);
        name.setText(configuration.getString("userName", "_Username_"));
        //school.setText(dataInterface.getSchool().name);


        int drawerItemId;
        if (savedInstanceState != null)
            drawerItemId = savedInstanceState.getInt("drawerItemId", R.id.action_dashboard);
        else {
            drawerItemId = configuration.getInt("drawerItemId", R.id.action_dashboard);
            navigationView.setCheckedItem(drawerItemId);
        }
        MenuItem item = navigationView.getMenu().findItem(drawerItemId);
        if (item != null)
            onNavigationItemSelected(item);
        else
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.action_dashboard));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("drawerItemId", drawerItemId);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            currentFragment=null;
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        ((AppBarLayout)findViewById(R.id.activity_main_app_bar_layout)).setExpanded(true);
        if (drawerItemId == item.getItemId()){
            currentFragment=null;
            return true;
        }

        drawerItemId = item.getItemId();

        if (drawerItemId == R.id.action_dashboard || drawerItemId == R.id.action_ideas)
            configuration.edit().putInt("drawerItemId", drawerItemId).apply();
        else if (drawerItemId == R.id.action_share)
            sendShareMessage();

        int position = 0;
        for (int i = 0; i < fragmentIds.length; i++)
            if (drawerItemId == fragmentIds[i]) {
                position = i;
                break;
            }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(navigationView.getMenu().findItem(fragmentIds[position]).getTitle());
        currentFragment = navigationFragments[position];

        if(currentFragment instanceof FloatingActionButtonHandler)
            ((FloatingActionButtonHandler)currentFragment).handleFloatingActionButton(floatingActionButton);
        else
            floatingActionButton.setVisibility(View.GONE);

        if (currentFragment instanceof TabLayoutHandler)
            ((TabLayoutHandler) currentFragment).handleTabLayout(tabLayout);
        else
            tabLayout.setVisibility(View.GONE);
        return true;
    }

    public void sendShareMessage() {
        Random random = new Random();
        /*ArrayList<Event> tasks = dataInterface.getEvents(Codes.task);
        ArrayList<Event> appointments = dataInterface.getEvents(Codes.appointment);

        StringBuilder builder = new StringBuilder();

        if (!tasks.isEmpty()) {
            builder.append(getString(R.string.title_task_list)).append(": \n");
            for (Event task : tasks)
                if (task.groupId != 0)
                    builder.append(task.subject).append(": ").append(task.description)
                            .append(getString(R.string.message_task_share))
                            .append(Dates.getStringRelative(this, task.date)).append(")\n\n");
        }
        if (!appointments.isEmpty()) {
            builder.append(getString(R.string.title_appointment_list)).append(": \n");
            for (Event appointment : appointments) {
                if (appointment.groupId != 0)
                    builder.append(appointment.subject).append(": ").append(appointment.description)
                            .append(getString(R.string.message_appointment_share))
                            .append(Dates.getStringRelative(this, appointment.date)).append(")\n\n");
            }
        }
        if (builder.length() > 0)
            builder.delete(builder.length() - 2, builder.length());

        int r = random.nextInt(4);
        builder.append("\n\n");
        builder.append(getString(R.string.promotion_basic));
        if (r > 0)
            builder.append(getString(R.string.promotion_advantages));
        builder.append(getResources().getStringArray(R.array.promotions)[r]);

        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, builder.toString()), getString(R.string.action_share)));*/
    }

    public interface FloatingActionButtonHandler{
        void handleFloatingActionButton(FloatingActionButton floatingActionButton);
    }

    public interface TabLayoutHandler{
        void handleTabLayout(TabLayout tabLayout);
    }
}