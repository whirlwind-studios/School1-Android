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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.CourseAdapter;
import com.whirlwind.school1.adapter.FilterAdapter;
import com.whirlwind.school1.models.Group;
import com.whirlwind.school1.models.UserGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoursesFragment extends Fragment implements SearchView.OnQueryTextListener, ChildEventListener {

    // TODO: Orientation change
    private CourseAdapter adapter;
    private Map<String, Group> courses = new HashMap<>();
    private Map<String, UserGroup> userGroups = new HashMap<>();
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

        FirebaseDatabase.getInstance().getReference()
                .child("courses")
                .addChildEventListener(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(auth.getCurrentUser().getUid())
                    .child("userGroups")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            userGroups.put(dataSnapshot.getKey(), (UserGroup) dataSnapshot.getValue());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            userGroups.put(dataSnapshot.getKey(), (UserGroup) dataSnapshot.getValue());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            userGroups.remove(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

        return recyclerView;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        courses.put(dataSnapshot.getKey(), (Group) dataSnapshot.getValue());
        onQueryTextChange(lastQuery);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        courses.put(dataSnapshot.getKey(), (Group) dataSnapshot.getValue());
        onQueryTextChange(lastQuery);

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        courses.remove(dataSnapshot.getKey());
        onQueryTextChange(lastQuery);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
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
        ArrayList<CourseAdapter.SortableCourse> filtered = new ArrayList<>(courses.size() / (newText.length() + 1));
        for (Map.Entry<String, Group> entry : courses.entrySet()) {
            Group course = entry.getValue();
            int sortIndex = FilterAdapter.filter(newText, course.name, course.description);
            if (sortIndex != -1) {
                if (userGroups.containsKey(entry.getKey()))
                    sortIndex += Integer.MAX_VALUE / 3;
                filtered.add(new CourseAdapter.SortableCourse(course, sortIndex));
            }
        }
        adapter.setCourses(filtered);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}