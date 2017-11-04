package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.models.Group;

import java.util.HashMap;
import java.util.Map;

public class SchoolCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_create);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_school_signup);
        }
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
        TextInputEditText nameEditText = findViewById(R.id.activity_school_create_name),
                passwordEditText = findViewById(R.id.activity_school_create_password);

        Map<String, Object> group = new HashMap<>();
        group.put("name", nameEditText.getText().toString());
        group.put("password", passwordEditText.getText().toString());
        group.put("type", Group.TYPE_SCHOOL);

        FirebaseFirestore.getInstance()
                .collection("groups")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        finish();
                    }
                });
    }
}
