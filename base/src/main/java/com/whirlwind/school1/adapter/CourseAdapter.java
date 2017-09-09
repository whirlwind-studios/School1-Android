package com.whirlwind.school1.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.models.Group;
import com.whirlwind.school1.models.UserGroup;

import java.util.ArrayList;
import java.util.Collections;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private ArrayList<SortableCourse> courses = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_layout_course, parent, false);
        return new ViewHolder(view);
    }

    public void setCourses(ArrayList<SortableCourse> courses) {
        this.courses = courses;
        Collections.sort(courses);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Group course = courses.get(position).t; // wrong type

        holder.name.setText(course.name);

        int resId = (course.flags & UserGroup.ADMIN) != 0 ? R.string.message_course_admin : R.string.message_course_participant;
        holder.permission.setText(resId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new CoursePopup(coursesFragment, course).show();
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

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.row_layout_course_icon);
            name = itemView.findViewById(R.id.row_layout_course_name);
            permission = itemView.findViewById(R.id.row_layout_course_permission);
        }
    }
}
