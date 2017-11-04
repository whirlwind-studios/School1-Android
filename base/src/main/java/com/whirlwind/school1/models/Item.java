package com.whirlwind.school1.models;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.DashboardAdapter;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.helper.DateHelper;
import com.whirlwind.school1.popup.ItemPopup;

import java.util.Calendar;

@IgnoreExtraProperties
public class Item implements DashboardAdapter.RowItem, BackendHelper.Queryable, BackendHelper.ChildInterface {

    public static final int TASK = 0, APPOINTMENT = 1;
    // Properties
    public int type;
    public String subject, description;
    public long date;

    // Metadata
    @Exclude
    private String id;
    @Exclude
    private String parent;

    public Item() {
    }

    public Item(String subject, String description, long date) {
        this.subject = subject;
        this.description = description;
        this.date = date;
    }

    public Item(String subject, String description, long date, int type) {
        this.subject = subject;
        this.description = description;
        this.date = date;
        this.type = type;
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
        if (type == TASK)
            itemSubject.setBackgroundResource(R.drawable.circle_subject_task); // Task/ appointment
        else
            itemSubject.setBackgroundResource(R.drawable.circle_subject_appointment);
        itemSubject.setEnabled(parent == null); // private/shared indicator

        itemDescription.setText(description);

        if (displayDate(date)) {
            itemDate.setText(DateHelper.getStringRelative(view.getContext(), date));
            itemDate.setVisibility(View.VISIBLE);
            ((RelativeLayout.LayoutParams) itemDescription.getLayoutParams()).removeRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            itemDate.setVisibility(View.GONE);
            ((RelativeLayout.LayoutParams) itemDescription.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemPopup(Item.this).show();
            }
        });
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    @Exclude
    public String getId() {
        return id;
    }

    @Override
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Override
    @Exclude
    public String getParent() {
        return parent;
    }

    @Override
    @Exclude
    public void setParent(String id) {
        this.parent = id;
    }
}