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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private Button datePicker;
    private CheckBox shareCheckBox;
    private Spinner typeSpinner,
            courseSpinner;

    private long date;
    private int type = Item.TASK;

    private CourseSelectionAdapter courseSelectionAdapter = new CourseSelectionAdapter();

    private static CollectionReference getItemsReference(String groupId) {
        CollectionReference items;
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(groupId))
            items = FirebaseFirestore.getInstance()
                    .collection("users");
        else
            items = FirebaseFirestore.getInstance()
                    .collection("groups");
        return items.document(groupId).collection("items");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_item);

        Intent args = getIntent();

        Toolbar toolbar = findViewById(R.id.activity_config_item_toolbar);
        typeSpinner = findViewById(R.id.activity_config_item_spinner_type);
        subject = findViewById(R.id.activity_config_item_edit_text_subject);
        description = findViewById(R.id.activity_config_item_edit_text_description);
        datePicker = findViewById(R.id.activity_config_item_button_date_picker);
        shareCheckBox = findViewById(R.id.activity_config_item_checkbox_share);
        courseSpinner = findViewById(R.id.activity_config_item_spinner_course);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            if (args.getBooleanExtra("isNew", true))
                date = System.currentTimeMillis() / 1000;
            else {
                type = args.getIntExtra("type", 0);
                subject.setText(args.getStringExtra("subject"));
                description.setText(args.getStringExtra("description"));
                date = args.getLongExtra("date", System.currentTimeMillis() / 1000);
                shareCheckBox.setChecked(args.getStringExtra("groupId") != null);
                courseSpinner.setSelection(courseSelectionAdapter.getItemPosition(args.getStringExtra("groupId")));
            }
        } else {
            date = savedInstanceState.getLong("date");
            shareCheckBox.setChecked(args.getStringExtra("groupId") != null);
            courseSpinner.setSelection(courseSelectionAdapter.getItemPosition(args.getStringExtra("groupId")));
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
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        typeSpinner.setSelection(type);

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

        onCheckedChanged(shareCheckBox, shareCheckBox.isChecked());
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
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            shareCheckBox.setText(R.string.hint_share_checkbox_on);
            courseSpinner.setVisibility(View.VISIBLE);
        } else {
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
        outState.putString("groupId", shareCheckBox.isChecked() ? courseSelectionAdapter.getGroupId() : null);
        outState.putInt("type", type);
        outState.putLong("date", date);
    }

    private void done() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        Item item = new Item(subject.getText().toString(), description.getText().toString(),
                date, type);

        String groupId = courseSelectionAdapter.getGroupId();

        if (!shareCheckBox.isChecked())
            groupId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (getIntent().getBooleanExtra("isNew", true))
            getItemsReference(groupId).add(item);
        else {
            String id = getIntent().getStringExtra("id");
            getItemsReference(getIntent().getStringExtra("groupId")).document(id).delete();
            getItemsReference(groupId).document(id).set(item);
        }
        finish();
    }
}