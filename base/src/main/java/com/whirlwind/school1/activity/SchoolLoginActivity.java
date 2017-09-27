package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.models.Group;

import java.util.ArrayList;
import java.util.List;

public class SchoolLoginActivity extends BaseActivity implements View.OnClickListener {

    private AutoCompleteTextView nameAutoCompleteTextView;
    private TextInputEditText passwordEditText;
    private TextInputLayout nameLayout, passwordLayout;
    private TextView signupTextView;

    private List<Group> schools = new ArrayList<>();
    private ArrayAdapter<Group> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_login);

        nameAutoCompleteTextView = findViewById(R.id.activity_school_login_autocomplete_text_view_name);
        passwordEditText = findViewById(R.id.activity_school_login_edit_text_password);
        nameLayout = findViewById(R.id.activity_school_login_text_input_layout_name);
        passwordLayout = findViewById(R.id.activity_school_login_text_input_layout_password);
        signupTextView = findViewById(R.id.activity_school_login_signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_account_school);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schools);
        nameAutoCompleteTextView.setAdapter(adapter);

        // TODO: Cloud functions, on-the-fly suggestion updates, functionality
        FirebaseDatabase.getInstance().getReference()
                .child("schools")
                .addChildEventListener(new BackendHelper.ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Group school = dataSnapshot.getValue(Group.class);
                        if (school != null) {
                            school.setKey(dataSnapshot.getKey());
                            schools.add(school);
                            adapter.add(school);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Group school = dataSnapshot.getValue(Group.class);
                        if (school != null) {
                            school.setKey(dataSnapshot.getKey());
                            for (int i = 0; i < schools.size(); i++)
                                if (schools.get(i).getKey().equals(school.getKey())) {
                                    Group oldSchool = schools.get(i);
                                    adapter.remove(oldSchool);
                                    adapter.add(school);
                                    schools.set(i, school);
                                    break;
                                }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Group school = dataSnapshot.getValue(Group.class);
                        if (school != null) {
                            for (int i = 0; i < schools.size(); i++)
                                if (schools.get(i).getKey().equals(school.getKey())) {
                                    adapter.remove(schools.remove(i));
                                    break;
                                }
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onClick(v);
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.activity_school_login_button_login).setOnClickListener(this);

        String signupText = "Blubblub please change me";
        SpannableString spannableString = new SpannableString(signupText);
        spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupTextView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onClick(View v) {
        nameLayout.setErrorEnabled(false);
        passwordLayout.setErrorEnabled(false);
        String name = nameAutoCompleteTextView.getText().toString(),
                password = passwordEditText.getText().toString();
        if ("".equals(name))
            nameLayout.setError(getString(R.string.error_field_required));
        else if ("".equals(password))
            passwordLayout.setError(getString(R.string.error_field_required));
        //else dataInterface.loginSchool(this, name, password);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else
            return super.onOptionsItemSelected(item);

        return true;
    }
}