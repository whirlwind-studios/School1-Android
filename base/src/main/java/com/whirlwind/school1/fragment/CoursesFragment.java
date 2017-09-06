package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.whirlwind.school1.R;

public class CoursesFragment extends Fragment implements SearchView.OnQueryTextListener {

    // TODO: Orientation change

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
        /*adapter = new CourseAdapter2();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        dataInterface.syncGroups(false, new Backend.ResultCallback() {
            @Override
            protected void onGetResult(Object result) {
                courses = (ArrayList<Group>) result;
                onQueryTextChange("");
            }
        });
*/
        return recyclerView;
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
        /*ArrayList<CourseAdapter2.SortableCourse> filtered = new ArrayList<>();
        for (Group course : courses) {
            int sortIndex = FilterAdapter.filter(newText, course.name, course.description);
            if (sortIndex != -1)
                filtered.add(new CourseAdapter2.SortableCourse(course, sortIndex));
        }
        adapter.setCourses(filtered);*/
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
