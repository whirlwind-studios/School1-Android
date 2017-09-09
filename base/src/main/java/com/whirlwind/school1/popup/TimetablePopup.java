package com.whirlwind.school1.popup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.TextView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.base.DialogPopup;
import com.whirlwind.school1.models.Lesson;

import java.util.ArrayList;
import java.util.List;

public class TimetablePopup extends DialogPopup {

    private int maxLessonHour = 0,
            maxLessonDay = 0;

    private static int minOf(int a, int b) {
        return (a < b ? a : b);
    }

    @SuppressLint("InflateParams")
    @Override
    public void build() {
        List<Lesson> lessons = new ArrayList<>();
        if (maxLessonHour == 0) {
            for (Lesson lesson : lessons) {
                int time = lesson.time & 248;
                int day = lesson.time & 7;
                if (time > maxLessonHour)
                    maxLessonHour = time;
                if (day > maxLessonDay)
                    maxLessonDay = day;
            }
            maxLessonHour = maxLessonHour >> 3;
            maxLessonDay++;
            maxLessonHour++;
        }

        customView = LayoutInflater.from(context).inflate(R.layout.popup_timetable, null, false);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;

        GridLayout gridLayout = customView.findViewById(R.id.popup_timetable_grid_layout_lessons);
        gridLayout.setColumnCount(maxLessonDay);
        gridLayout.setRowCount(maxLessonHour);
        if (maxLessonHour > 6)
            gridLayout.addView(LayoutInflater.from(context).inflate(R.layout.popup_timetable_textview, gridLayout, false),
                    new GridLayout.LayoutParams(GridLayout.spec(6), GridLayout.spec(0)));

        for (Lesson lesson : lessons) {
            TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.popup_timetable_textview, gridLayout, false);

            textView.setText(getShorthand(lesson.name, width));

            GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(
                    GridLayout.spec((lesson.time & 248) >> 3), GridLayout.spec(lesson.time & 7));
            gridLayout.addView(textView, gridParams);
        }

        dialog = new AlertDialog.Builder(context).setTitle(R.string.title_lesson_list).setView(customView)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.text_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }

    private String getShorthand(String full, float width) {
        String splits[] = full.split(" ");

        if (width >= 820)
            return full;
        else if (full.length() < 8 && splits.length == 1)
            return full.substring(0, minOf((int) width / 150, full.length()));
        else if (splits.length == 1)
            return full.substring(0, minOf((int) width / 100, full.length()));
        else {
            StringBuilder builder = new StringBuilder();
            for (String split : splits) {
                if (!split.equals("und")) {
                    builder.append(split.substring(0, 1).toUpperCase());
                    if (width >= 600)
                        builder.append(split.substring(1, 2));
                }
            }
            return builder.toString();
        }
    }
}
