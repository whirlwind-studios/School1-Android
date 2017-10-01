package com.whirlwind.school1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.models.Group;

import java.util.ArrayList;
import java.util.List;

public class CourseSelectionAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private List<Group> courses = new ArrayList<>();
    private String groupId;
    private sharableListener sharableListener;

    public CourseSelectionAdapter() {
        databaseReference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("groups")
                .addChildEventListener(new BackendHelper.ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Boolean bool = dataSnapshot.getValue(Boolean.class);
                        if (bool != null) {
                            databaseReference
                                    .child("groups")
                                    .child(dataSnapshot.getKey())
                                    .addValueEventListener(new BackendHelper.ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Group course = dataSnapshot.getValue(Group.class);
                                            if (course != null) {
                                                course.setKey(dataSnapshot.getKey());
                                                for (int i = 0; i < courses.size(); i++)
                                                    if (courses.get(i).getKey().equals(course.getKey())) {
                                                        courses.set(i, course);
                                                        notifyDataSetChanged();
                                                        return;
                                                    }

                                                // Not already included, just added
                                                courses.add(course);
                                                notifyDataSetChanged();
                                                if (sharableListener != null)
                                                    sharableListener.sharable(true);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Boolean bool = dataSnapshot.getValue(Boolean.class);
                        if (bool != null && bool)
                            onChildAdded(dataSnapshot, s);
                        else if (bool != null)
                            onChildRemoved(dataSnapshot);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        for (int i = 0; i < courses.size(); i++)
                            if (courses.get(i).getKey().equals(dataSnapshot.getKey())) {
                                courses.remove(i);
                                notifyDataSetChanged();
                                if (courses.size() == 0 && sharableListener != null)
                                    sharableListener.sharable(false);
                                return;
                            }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                });
    }

    @Override
    public long getItemId(int i) {
        return courses.get(i).getKey().hashCode();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        groupId = courses.get(position).getKey();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int i) {
        return courses.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        Group course = (Group) getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(course.name);

        return convertView;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setSharableListener(CourseSelectionAdapter.sharableListener sharableListener) {
        this.sharableListener = sharableListener;
    }

    public interface sharableListener {
        void sharable(boolean sharable);
    }
}
