package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.models.Group;

import java.util.HashMap;
import java.util.Map;

public class SchoolCreateActivity extends BaseActivity {

    private TextInputEditText editText;
    private TextInputLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_create);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_school_signup);
        }

        editText = findViewById(R.id.activity_school_create_name);
        layout = findViewById(R.id.activity_school_create_input_layout);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    done();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_done)
            done();
        else
            return super.onOptionsItemSelected(item);
        return true;
    }

    private void done() {
        layout.setErrorEnabled(false);
        String name = editText.getText().toString();

        if ("".equals(name)) {
            layout.setError(getString(R.string.error_field_required));
            return;
        }

        Map<String, Object> group = new HashMap<>();
        group.put("name", name);
        group.put("type", Group.TYPE_SCHOOL);

        FirebaseFirestore.getInstance()
                .collection("groups")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Map<String, Object> userGroup = new HashMap<>();
                        userGroup.put("access_level", Group.ACCESS_LEVEL_MEMBER);

                        BackendHelper.getUserReference()
                                .collection("groups")
                                .document(documentReference.getId())
                                .set(userGroup);

                        Map<String, Object> school = new HashMap<>();
                        school.put("school", documentReference.getId());
                        BackendHelper.getUserReference()
                                .set(school);

                        finish();
                    }
                });
    }
}
