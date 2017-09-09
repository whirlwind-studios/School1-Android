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

import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.helper.ConfigurationHelper;
import com.whirlwind.school1.helper.DateHelper;
import com.whirlwind.school1.models.Item;
import com.whirlwind.school1.popup.ConfirmationPopup;
import com.whirlwind.school1.popup.DatePickerPopup;
import com.whirlwind.school1.popup.TimetablePopup;

import java.util.Calendar;

public class ConfigEventActivity extends BaseActivity {

    private AutoCompleteTextView subject;
    private TextInputEditText description;
    private long date;
    private int flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_event);

        final Intent args = getIntent();
        Toolbar toolbar = findViewById(R.id.activity_config_event_toolbar);
        subject = findViewById(R.id.activity_config_event_autocomplete_text_view_subject);
        description = findViewById(R.id.activity_config_event_edit_text_description);
        final Button datePicker = findViewById(R.id.activity_config_event_button_date_picker);
        CheckBox checkBox = findViewById(R.id.activity_config_event_checkbox_share);
        Spinner spinner = findViewById(R.id.activity_config_event_spinner);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            flags = args.getIntExtra("flags", ConfigurationHelper.getShareDefault(this) ? Item.PRIVATE : Item.SHARED);
            if (args.getBooleanExtra("isNew", true))
                date = System.currentTimeMillis() / 1000;
            else {
                date = args.getLongExtra("date", System.currentTimeMillis());
                subject.setText(args.getStringExtra("subject"));
                description.setText(args.getStringExtra("description"));
            }
        } else {
            flags = savedInstanceState.getInt("flags");
            date = savedInstanceState.getLong("date");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        spinner.setAdapter(new ArrayAdapter<String>(toolbar.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                TextViewCompat.setTextAppearance((TextView) view.findViewById(android.R.id.text1), R.style.TextAppearance_AppCompat_Title);
                return view;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flags &= ~Item.TYPE_MASK;
                flags |= position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setSelection(flags & Item.TYPE_MASK);

        /*if (Group.getAdminGroups(dataInterface.getCourses()).isEmpty()) {
            checkBox.setVisibility(View.GONE);
            flags &= ~Item.PRIVATE;
        } else {*/
        checkBox.setChecked((flags & Item.PRIVATE) != 0);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        flags |= Item.PRIVATE;
                    else
                        flags &= ~Item.SHARED;
                }
            });
        //}

        datePicker.setText(DateHelper.getStringRelative(this, date));
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerPopup(DateHelper.getCalendar(date), new DatePickerPopup.OnDateSetListener() {
                    @Override
                    public void onDateSet(Context context, int year, int month, int dayOfMonth) {
                        if (context instanceof ConfigEventActivity) {
                            Button datePicker = ((Activity) context).findViewById(R.id.activity_config_event_button_date_picker);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            datePicker.setText(DateHelper.getStringRelative(context, calendar));

                            date = DateHelper.getDate(year, month, dayOfMonth);
                        }
                    }
                }).show(ConfigEventActivity.this);
            }
        });

        //subject.setAdapter(new FilterAdapter<>(this, android.R.layout.simple_list_item_1, Lesson.removeDuplicates(dataInterface.getLessons())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_timetable) {
            new TimetablePopup().show(this);
        } else if (item.getItemId() == R.id.action_done)
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
        long relativeDays = DateHelper.getRelativeDays(date);
        if (relativeDays == 0 || description.length() == 0 || description.length() == 0) {
            StringBuilder builder = new StringBuilder();
            if (subject.length() == 0 || description.length() == 0) {
                builder.append(getString(R.string.info_empty_fields));
                if (relativeDays == 0) {
                    builder.append(getString(R.string.info_and));
                    builder.append(getString(R.string.info_date_today));
                }
            } else if (relativeDays == 0)
                builder.append(getString(R.string.info_date_today));

            builder.append(getString(R.string.info_save_anyway));

            String string = builder.toString();
            string = string.substring(0, 1).toUpperCase() + string.substring(1);
            new ConfirmationPopup(getString(R.string.text_confirm_button), string, new Runnable() {
                @Override
                public void run() {
                    /*if (getIntent().getBooleanExtra("isNew", true))
                        dataInterface.addEvent(ConfigEventActivity.this,
                                new Event(subject.getText().toString(), description.getText().toString(),
                                        date, flags), null);
                    else
                        dataInterface.editEvent(ConfigEventActivity.this,
                                new Event(getIntent().getLongExtra("id", 0), 0, (byte) 0, subject.getText().toString(), description.getText().toString(), date, flags),
                                getIntent().getIntExtra("flags", Codes.public_), null);*/
                }
            }).show(ConfigEventActivity.this);
        } else {
            /*if (getIntent().getBooleanExtra("isNew", true))
                dataInterface.addEvent(this,
                        new Event(subject.getText().toString(), description.getText().toString(),
                                date, flags), null);
            else
                dataInterface.editEvent(this,
                        new Event(getIntent().getLongExtra("id", 0), 0, (byte) 0, subject.getText().toString(), description.getText().toString(), date, flags),
                        getIntent().getIntExtra("flags", Codes.public_), null);*/
        }
        finish();
    }
}