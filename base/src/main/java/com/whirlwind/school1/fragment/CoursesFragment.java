package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.CourseAdapter;
import com.whirlwind.school1.adapter.FilterAdapter;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.models.Group;
import com.whirlwind.school1.popup.TextPopup;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment implements SearchView.OnQueryTextListener, ValueEventListener {

    // TODO: Orientation change
    private CourseAdapter adapter;
    private List<Group> groups = new ArrayList<>(),
            userGroups = new ArrayList<>();
    private String lastQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Query", "Create");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", lastQuery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_courses, container, false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CourseAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("groups")
                .addChildEventListener(new UserGroupListener());

        return recyclerView;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Group group = dataSnapshot.getValue(Group.class);

        // Valid value, added or changed
        if (group != null) {
            group.setKey(dataSnapshot.getKey());

            // Changed
            for (int i = 0; i < groups.size(); i++)
                if (groups.get(i).getKey().equals(dataSnapshot.getKey())) {
                    groups.set(i, group);
                    onQueryTextChange(lastQuery);
                    return;
                }

            // Added
            groups.add(group);
            onQueryTextChange(lastQuery);
        }
        // deleted
        else if (!dataSnapshot.exists()) {
            for (int i = 0; i < groups.size(); i++)
                if (groups.get(i).equals(dataSnapshot.getKey())) {
                    groups.remove(i);
                    onQueryTextChange(lastQuery);
                    return;
                }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        new TextPopup(R.string.error_title, databaseError.getMessage()).show();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && lastQuery != null)
            lastQuery = savedInstanceState.getString("query");
        if (lastQuery == null)
            lastQuery = "";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_courses, menu);
        MenuItem item = menu.findItem(R.id.action_courses_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setOnQueryTextListener(this);
        if (!"".equals(lastQuery)) {
            searchView.setQuery(lastQuery, false);
            item.expandActionView();
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        lastQuery = newText;

        ArrayList<CourseAdapter.SortableCourse> filtered = new ArrayList<>(groups.size() / (newText.length() + 1));
        for (Group group : groups) {
            int sortIndex = FilterAdapter.filter(newText, group.name, group.description);
            if (sortIndex != -1) {
                for (Group userGroup : userGroups)
                    if (userGroup.getKey().equals(group.getKey())) {
                        sortIndex += Integer.MAX_VALUE / 3;
                        break;
                    }

                filtered.add(new CourseAdapter.SortableCourse(group, sortIndex));
            }
        }
        adapter.setCourses(filtered);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private class UserGroupListener extends BackendHelper.ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            FirebaseDatabase.getInstance().getReference()
                    .child("groups")
                    .child(dataSnapshot.getKey())
                    .child("subGroups")
                    .addChildEventListener(new SubGroupListener());

            Group group = new Group();
            group.setKey(dataSnapshot.getKey());
            userGroups.add(group);
            adapter.setUserGroups(userGroups);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            for (int i = 0; i < userGroups.size(); i++)
                if (userGroups.get(i).getKey().equals(dataSnapshot.getKey())) {
                    userGroups.remove(i);
                    adapter.notifyDataSetChanged();
                    return;
                }
        }
    }

    private class SubGroupListener extends BackendHelper.ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            FirebaseDatabase.getInstance().getReference()
                    .child("groups")
                    .child(dataSnapshot.getKey())
                    .addValueEventListener(CoursesFragment.this);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }
    }
}