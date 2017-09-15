package com.whirlwind.school1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.models.Lesson;

import java.util.ArrayList;

public class TimetableConfigAdapter extends BaseAdapter {
    private final ArrayList<Lesson> gridLessons = new ArrayList<>();
    private final ArrayList<Lesson> changes = new ArrayList<>();
    private final String groupId = "";

    /*public TimetableConfigAdapter(long groupId, ArrayList<Lesson> changes) {
        this.groupId = groupId;
        this.changes = changes;

        gridLessons.clear();
        for (int i = 0; i < 55; i++)
            gridLessons.add(new Lesson());

        ArrayList<Lesson> lessons;
        if (groupId != -1)
            lessons = dataInterface.getLessons(groupId);
        else
            lessons = dataInterface.getLessons();

        for (Lesson lesson : lessons)
            gridLessons.set(((lesson.time & 248) >> 3) * 5 + (lesson.time & 7), lesson);
    }*/

    @Override
    public int getCount() {
        return gridLessons.size();
    }

    @Override
    public Object getItem(int position) {
        return gridLessons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Lesson lesson = (Lesson) getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_grid_text_view, parent, false);

        TextView textView = (TextView) convertView;
        textView.setText(lesson.title);
        final int day = (position % 5);
        int hourOfDay = (position - day) / 5;

        // Just a boolean to give selector a value
        convertView.setActivated(hourOfDay == 6);
        convertView.setBackgroundResource(R.drawable.timetable_cell_background);

        /*if (groupId > 0)
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LessonConfigPopup(position, groupId, lesson, changes, TimetableConfigAdapter.this).show();
                }
            });
        else {
            final String title = LessonConfigPopup.getPopupTitle(parent.getContext(), position);
            if ((lesson.flags & Codes.public_) != 0)
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Group> courses = dataInterface.getCourses();
                        for (Group course : courses) {
                            if (course.id == lesson.groupId) {
                                new TextPopup(title,
                                        lesson.name + parent.getContext().getString(R.string.info_lesson_course) + course.name).show();
                                return;
                            }
                        }
                    }
                });
            else
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LessonConfigPopup(position, groupId, lesson, changes, TimetableConfigAdapter.this).show();
                    }
                });
        }*/
        return convertView;
    }

    /*public void update(String name, int position) {
        Lesson lesson = gridLessons.get(position);
        int day = position % 5;
        int hourOfDay = (position - day) / 5;
        byte time = (byte) (day | (hourOfDay << 3));

        Lesson change;

        if (!name.equals(lesson.name)) {
            for (int i = 0; i < changes.size(); i++) {
                change = changes.get(i);
                if (change.time == time) {
                    change.id = lesson.id;
                    change.name = name;
                    change.flags = groupId != -1 ? Codes.public_ : Codes.private_;
                    changes.set(i, change);
                    update(change);
                    return;
                }
            }
            change = new Lesson(lesson.id, name, time, groupId != -1 ? Codes.public_ : Codes.private_);
            changes.add(change);
            update(change);
        }
    }

    private void update(Lesson change) {
        gridLessons.set(((change.time & 248) >> 3) * 5 + (change.time & 7), change);
        notifyDataSetChanged();
    }*/
}