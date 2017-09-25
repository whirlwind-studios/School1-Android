package com.whirlwind.school1.models;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.IgnoreExtraProperties;
import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.DashboardAdapter;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.helper.DateHelper;

import java.util.Calendar;

@IgnoreExtraProperties
public class Item implements DashboardAdapter.RowItem, BackendHelper.Queryable {

    public static final int TASK = 0, APPOINTMENT = 1, TYPE_MASK = 7; // max 8 different types
    public static final int PRIVATE = 0, SHARED = 8;
    public String groupId;
    public int flags;
    // Properties
    public String subject, description;
    public long date;
    // Metadata
    private String key;

    public Item() {
    }

    public Item(String subject, String description, long date) {
        this.subject = subject;
        this.description = description;
        this.date = date;
    }

    public Item(String groupId, String subject, String description, long date, int flags) {
        this.groupId = groupId;
        this.subject = subject;
        this.description = description;
        this.date = date;
        this.flags = flags;
    }

    private static boolean displayDate(long date) {
        long relativeDays = DateHelper.getRelativeDays(date);
        if (relativeDays == 0)
            return false;
        if (relativeDays == 1) {
            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            return today == Calendar.FRIDAY || today == Calendar.SUNDAY;
        }
        return true;
    }

    @Override
    public void populate(View view, int position) {
        TextView itemSubject = view.findViewById(R.id.row_layout_dashboard_subject),
                itemDescription = view.findViewById(R.id.row_layout_dashboard_description),
                itemDate = view.findViewById(R.id.row_layout_dashboard_date);

        itemSubject.setText(subject.substring(0, Math.min(subject.length(), 3)));
        if ((flags & TYPE_MASK) == TASK)
            itemSubject.setBackgroundResource(R.drawable.circle_subject_task); // Task/ appointment
        else
            itemSubject.setBackgroundResource(R.drawable.circle_subject_appointment);
        itemSubject.setEnabled((flags & SHARED) == PRIVATE); // private/shared indicator

        itemDescription.setText(description);

        if (displayDate(date)) {
            itemDate.setText(DateHelper.getStringRelative(view.getContext(), date));
            itemDate.setVisibility(View.VISIBLE);
            ((RelativeLayout.LayoutParams) itemDescription.getLayoutParams()).removeRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            itemDate.setVisibility(View.GONE);
            ((RelativeLayout.LayoutParams) itemDescription.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
        }
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }
}