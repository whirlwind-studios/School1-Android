package com.whirlwind.school1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.CourseSelectionAdapter;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.helper.DateHelper;
import com.whirlwind.school1.models.Item;
import com.whirlwind.school1.popup.DatePickerPopup;
import com.whirlwind.school1.popup.TimetablePopup;

import java.util.Calendar;

public class ConfigItemActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private AutoCompleteTextView subject;
    private TextInputEditText description;
    private CheckBox shareCheckBox;
    private Spinner typeSpinner,
            courseSpinner;

    private long date;
    private int flags;

    private CourseSelectionAdapter courseSelectionAdapter = new CourseSelectionAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_item);

        Intent args = getIntent();

        Toolbar toolbar = findViewById(R.id.activity_config_item_toolbar);
        subject = findViewById(R.id.activity_config_item_edit_text_subject);
        description = findViewById(R.id.activity_config_item_edit_text_description);
        Button datePicker = findViewById(R.id.activity_config_item_button_date_picker);
        shareCheckBox = findViewById(R.id.activity_config_item_checkbox_share);
        typeSpinner = findViewById(R.id.activity_config_item_spinner_type);
        courseSpinner = findViewById(R.id.activity_config_item_spinner_course);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            if (args.getBooleanExtra("isNew", true)) {
                flags &= ~Item.SHARED;
                date = System.currentTimeMillis() / 1000;
            }
            else {
                date = args.getLongExtra("date", System.currentTimeMillis() / 1000);
                subject.setText(args.getStringExtra("subject"));
                description.setText(args.getStringExtra("description"));
            }
        } else {
            flags = savedInstanceState.getInt("flags");
            date = savedInstanceState.getLong("date");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        typeSpinner.setAdapter(new ArrayAdapter<String>(toolbar.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                TextViewCompat.setTextAppearance((TextView) view.findViewById(android.R.id.text1), R.style.TextAppearance_AppCompat_Title);
                return view;
            }
        });
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flags &= ~Item.TYPE_MASK;
                flags |= position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        typeSpinner.setSelection(flags & Item.TYPE_MASK);

        //subject.setAdapter(new FilterAdapter<>(this, android.R.layout.simple_list_item_1, Lesson.removeDuplicates(dataInterface.getLessons())));

        datePicker.setText(DateHelper.getStringRelative(this, date));
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerPopup(DateHelper.getCalendar(date), new DatePickerPopup.OnDateSetListener() {
                    @Override
                    public void onDateSet(Context context, int year, int month, int dayOfMonth) {
                        if (context instanceof ConfigItemActivity) {
                            Button datePicker = ((Activity) context).findViewById(R.id.activity_config_item_button_date_picker);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            datePicker.setText(DateHelper.getStringRelative(context, calendar));

                            date = DateHelper.getDate(year, month, dayOfMonth);
                        }
                    }
                }).show(ConfigItemActivity.this);
            }
        });


        shareCheckBox.setChecked((flags & Item.SHARED) != 0);
        onCheckedChanged(shareCheckBox, (flags & Item.SHARED) != 0);
        shareCheckBox.setOnCheckedChangeListener(this);

        courseSpinner.setAdapter(courseSelectionAdapter);
        courseSpinner.setOnItemSelectedListener(courseSelectionAdapter);
        courseSelectionAdapter.setSharableListener(new CourseSelectionAdapter.sharableListener() {
            @Override
            public void sharable(boolean sharable) {
                if (sharable)
                    shareCheckBox.setEnabled(true);
                else {
                    shareCheckBox.setEnabled(false);
                    shareCheckBox.setChecked(false);
                    onCheckedChanged(shareCheckBox, false);
                    flags &= ~Item.SHARED;
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            flags |= Item.SHARED;
            shareCheckBox.setText(R.string.hint_share_checkbox_on);
            courseSpinner.setVisibility(View.VISIBLE);
        } else {
            flags &= ~Item.SHARED;
            shareCheckBox.setText(R.string.hint_share_checkbox_off);
            courseSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Cannot use switch here due to library resource id's
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_timetable)
            new TimetablePopup().show(this);
        else if (item.getItemId() == R.id.action_done)
            done();
        else
            return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("flags", flags);
        outState.putLong("date", date);
    }

    private void done() {
        String groupId = courseSelectionAdapter.getGroupId();
        if (!shareCheckBox.isChecked())
            groupId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference items = FirebaseDatabase.getInstance().getReference()
                .child("items");

        Item item = new Item(groupId, subject.getText().toString(), description.getText().toString(),
                date, flags);
        if (getIntent().getBooleanExtra("isNew", true))
            items.push().setValue(item);
        else
            items.child(getIntent().getStringExtra("uid")).setValue(item);
        finish();
    }
}