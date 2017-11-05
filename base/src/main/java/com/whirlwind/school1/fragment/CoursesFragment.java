package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.whirlwind.school1.R;
import com.whirlwind.school1.activity.ConfigCourseActivity;
import com.whirlwind.school1.activity.MainActivity;
import com.whirlwind.school1.adapter.CourseAdapter;
import com.whirlwind.school1.adapter.FilterAdapter;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.models.Group;

import java.util.ArrayList;
import java.util.List;

// Implements last stage query event listener
public class CoursesFragment extends Fragment implements EventListener<QuerySnapshot>, SearchView.OnQueryTextListener, MainActivity.FloatingActionButtonHandler {

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

        BackendHelper.getUserReference()
                .collection("groups")
                .addSnapshotListener(new UserGroupListener());

        return recyclerView;
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED: {
                    Group group = change.getDocument().toObject(Group.class);
                    group.setId(change.getDocument().getId());

                    groups.add(group);
                }
                break;
                case MODIFIED: {
                    Group group = change.getDocument().toObject(Group.class);
                    group.setId(change.getDocument().getId());

                    for (int i = 0; i < groups.size(); i++) {
                        if (group.getId().equals(groups.get(i).getId())) {
                            groups.set(i, group);
                            break;
                        }
                    }
                }
                break;
                case REMOVED: {
                    String id = change.getDocument().getId();
                    for (int i = 0; i < groups.size(); i++)
                        if (id.equals(groups.get(i).getId())) {
                            groups.remove(i);
                            break;
                        }
                }
                break;
            }
        }

        onQueryTextChange(lastQuery);
    }

    @Override
    public void handleFloatingActionButton(FloatingActionButton floatingActionButton) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ConfigCourseActivity.class));
            }
        });
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
            int sortIndex = FilterAdapter.filter(newText, group.name);
            if (sortIndex != -1) {
                for (Group userGroup : userGroups)
                    if (userGroup.getId().equals(group.getId())) {
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

    // First stage listener, adds a SubGroupListener for every group in users group collection
    private class UserGroupListener implements EventListener<QuerySnapshot> {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED: {
                        Group group = new Group();
                        group.setId(change.getDocument().getId());
                        userGroups.add(group);
                        adapter.setUserGroups(userGroups);

                        FirebaseFirestore.getInstance()
                                .collection("groups")
                                .whereEqualTo("parentGroup", change.getDocument().getId())
                                .addSnapshotListener(CoursesFragment.this);
                    }
                    break;
                    case REMOVED: {
                        for (int i = 0; i < userGroups.size(); i++)
                            if (userGroups.get(i).getId().equals(change.getDocument().getId())) {
                                userGroups.remove(i);
                                adapter.notifyDataSetChanged();
                                return;
                            }
                    }
                    break;
                }
            }
        }
    }
}