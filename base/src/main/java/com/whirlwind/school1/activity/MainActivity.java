package com.whirlwind.school1.activity;

import android.app.Fragment;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.fragment.AboutFragment;
import com.whirlwind.school1.fragment.AccountFragment;
import com.whirlwind.school1.fragment.CoursesFragment;
import com.whirlwind.school1.fragment.DashboardFragment;
import com.whirlwind.school1.fragment.IdeasFragment;
import com.whirlwind.school1.fragment.SettingsFragment;
import com.whirlwind.school1.fragment.TimetableFragment;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.helper.DateHelper;
import com.whirlwind.school1.models.Item;
import com.whirlwind.school1.popup.TextPopup;

import java.util.LinkedList;
import java.util.List;
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

    private View headerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton floatingActionButton;
    private TabLayout tabLayout;

    private Fragment currentFragment;
    private int drawerItemId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (auth.getCurrentUser() == null)
            startActivity(new Intent(this, SigninActivity.class));
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
                updateFragment();
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        if (auth.getCurrentUser() != null) {
            TextView name = headerView.findViewById(R.id.navigation_header_layout_name);
            name.setText(auth.getCurrentUser().getDisplayName());

            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(auth.getCurrentUser().getUid())
                    .child("schoolName").addListenerForSingleValueEvent(new BackendHelper.ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView school = headerView.findViewById(R.id.navigation_header_layout_school);
                    school.setText(String.valueOf(dataSnapshot.getValue()));
                }
            });
        }

        int drawerItemId;
        if (savedInstanceState != null)
            drawerItemId = savedInstanceState.getInt("drawerItemId", R.id.action_dashboard);
        else {
            drawerItemId = configuration.getInt("drawerItemId", R.id.action_dashboard);
            navigationView.setCheckedItem(drawerItemId);
        }

        int position = getItemIndex(drawerItemId);
        MenuItem item = navigationView.getMenu()
                .findItem(fragmentIds[position]);
        onNavigationItemSelected(item);
        if (savedInstanceState == null)
            updateFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("drawerItemId", drawerItemId);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            currentFragment = null;
        } else
            super.onBackPressed();
    }

    private int getItemIndex(int id) {
        for (int i = 0; i < fragmentIds.length; i++)
            if (id == fragmentIds[i])
                return i;
        return 0;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        ((AppBarLayout) findViewById(R.id.activity_main_app_bar_layout)).setExpanded(true);

        int itemId = item.getItemId();

        // Use previous drawerItemId
        if (itemId == R.id.action_dashboard || itemId == R.id.action_ideas)
            configuration.edit().putInt("drawerItemId", drawerItemId).apply();
        else if (itemId == R.id.action_share) {
            sendShareMessage();
            itemId = drawerItemId;
        }
        if (drawerItemId == itemId) {
            currentFragment = null;
            return true;
        }

        // Update drawerItemId
        drawerItemId = itemId;

        int position = getItemIndex(drawerItemId);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(navigationView.getMenu().findItem(fragmentIds[position]).getTitle());
        currentFragment = navigationFragments[position];

        if (currentFragment instanceof FloatingActionButtonHandler)
            ((FloatingActionButtonHandler) currentFragment).handleFloatingActionButton(floatingActionButton);
        else
            floatingActionButton.setVisibility(View.GONE);

        if (currentFragment instanceof TabLayoutHandler)
            ((TabLayoutHandler) currentFragment).handleTabLayout(tabLayout);
        else
            tabLayout.setVisibility(View.GONE);
        return true;
    }

    private void updateFragment() {
        if (currentFragment != null)
            getFragmentManager().beginTransaction().replace(R.id.activity_main_container, currentFragment).commit();
    }

    public void sendShareMessage() {
        FirebaseDatabase.getInstance().getReference().child("items")
                .addListenerForSingleValueEvent(new BackendHelper.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Item> tasks = new LinkedList<>(),
                                appointments = new LinkedList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Item item = snapshot.getValue(Item.class);
                            new TextPopup("Item", String.valueOf(snapshot.getValue())).show();
                            if (item != null && (item.flags & Item.SHARED) != 0) {
                                int flags = item.flags & Item.TYPE_MASK;
                                if (flags == Item.TASK)
                                    tasks.add(item);
                                else if (flags == Item.APPOINTMENT)
                                    appointments.add(item);
                            }
                        }

                        StringBuilder builder = new StringBuilder();

                        if (!tasks.isEmpty()) {
                            builder.append(getString(R.string.title_task_list)).append(": \n");
                            for (Item task : tasks)
                                builder.append(task.subject).append(": ").append(task.description)
                                        .append(getString(R.string.message_task_share))
                                        .append(DateHelper.getStringRelative(MainActivity.this, task.getDate())).append(")\n\n");
                        }
                        if (!appointments.isEmpty()) {
                            builder.append(getString(R.string.title_appointment_list)).append(": \n");
                            for (Item appointment : appointments)
                                builder.append(appointment.subject).append(": ").append(appointment.description)
                                        .append(getString(R.string.message_appointment_share))
                                        .append(DateHelper.getStringRelative(MainActivity.this, appointment.getDate())).append(")\n\n");
                        }
                        if (builder.length() > 0)
                            builder.delete(builder.length() - 2, builder.length());

                        int r = new Random().nextInt(4);
                        builder.append("\n\n");
                        builder.append(getString(R.string.promotion_basic));
                        if (r > 0)
                            builder.append(getString(R.string.promotion_advantages));
                        builder.append(getResources().getStringArray(R.array.promotions)[r]);

                        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain")
                                .putExtra(Intent.EXTRA_TEXT, builder.toString()), getString(R.string.action_share)));
                    }
                });

    }

    public interface FloatingActionButtonHandler {
        void handleFloatingActionButton(FloatingActionButton floatingActionButton);
    }

    public interface TabLayoutHandler {
        void handleTabLayout(TabLayout tabLayout);
    }
}