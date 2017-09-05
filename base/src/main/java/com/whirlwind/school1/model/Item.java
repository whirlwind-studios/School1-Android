package com.whirlwind.school1.model;

import android.view.View;
import android.widget.TextView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.DashboardAdapter;

import java.util.Random;

public class Item implements DashboardAdapter.RowItem {

    public String subject,description,date; // TEMP

    public Item(String subject,String description, String date){
        this.subject=subject;
        this.description=description;
        this.date=date;
    }

    @Override
    public void populate(View view) {
        TextView itemSubject=view.findViewById(R.id.row_layout_dashboard_subject),
                itemDescription=view.findViewById(R.id.row_layout_dashboard_description),
                itemDate=view.findViewById(R.id.row_layout_dashboard_date);
        itemSubject.setText(subject.substring(0,3));
        Random random=new Random();
        if(random.nextBoolean())
            itemSubject.setBackgroundResource(R.drawable.circle_subject_task); // Task/ appointment
        itemSubject.setEnabled(random.nextBoolean()); // private/public indicator
        itemDescription.setText(description);
        itemDate.setText(date);
    }
}
