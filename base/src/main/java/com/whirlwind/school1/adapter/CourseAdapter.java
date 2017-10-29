package com.whirlwind.school1.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whirlwind.school1.R;
import com.whirlwind.school1.models.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<SortableCourse> courses = new ArrayList<>();
    private List<Group> userGroups = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_layout_course, parent, false);
        return new ViewHolder(view);
    }

    public void setCourses(List<SortableCourse> courses) {
        this.courses = courses;
        Collections.sort(courses);
        notifyDataSetChanged();
    }

    public void setUserGroups(List<Group> userGroups) {
        this.userGroups = userGroups;
        Collections.sort(courses);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Group course = courses.get(position).t; // wrong type

        holder.name.setText(course.name);

        //int resId = course. ? R.string.message_course_admin : R.string.message_course_participant;
        //holder.permission.setText(resId);
        // TODO: definitely needs some refactoring...
        holder.permission.setText(course.description);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CourseActivity for logging out, viewing course specific lessons, viewing members, adding admins etc
            }
        });

        for (Group userGroup : userGroups)
            if (userGroup.getKey().equals(course.getKey())) {

                holder.add.setVisibility(View.GONE);
                return;
            }

        holder.add.setVisibility(View.VISIBLE);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("groups")
                        .document(course.getKey())
                        .set(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class SortableCourse extends FilterAdapter.Sortable<Group> {
        public SortableCourse(Group course, int sortIndex) {
            super(course, sortIndex);
        }

        @Override
        public int compareTo(@NonNull FilterAdapter.Sortable<Group> o) {
            int diff = o.sortIndex - sortIndex;
            if (diff != 0)
                return diff;
            else
                return t.name.toLowerCase().compareTo(o.t.name.toLowerCase());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon, name, permission;
        ImageView add;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.row_layout_course_icon);
            name = itemView.findViewById(R.id.row_layout_course_name);
            permission = itemView.findViewById(R.id.row_layout_course_permission);
            add = itemView.findViewById(R.id.row_layout_course_add);
        }
    }
}
