package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.models.Group;
import com.whirlwind.school1.popup.TextPopup;

public class ConfigCourseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_course);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Cannot use switch here due to library resource id's
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_done) {
            TextView textView = findViewById(R.id.activity_config_course_name);
            String name = textView.getText().toString();


            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference userReference = reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            final Group group = new Group();
            group.name = name;
            group.type = Group.TYPE_COURSE;

            userReference.child("schoolId").addListenerForSingleValueEvent(new BackendHelper.ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class) == null)
                        new TextPopup(R.string.error_title, "Youre not logged into a school").show();
                    else {
                        group.parentGroup = dataSnapshot.getValue(String.class);
                        String key = reference.child("groups")
                                .push().getKey();
                        group.setKey(key);
                        reference.child("groups")
                                .child(key)
                                .setValue(group)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userReference.child("groups")
                                                .child(group.getKey())
                                                .setValue(true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        reference.child("groups")
                                                                .child(group.parentGroup)
                                                                .child("subGroups")
                                                                .child(group.getKey())
                                                                .setValue(true)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        finish();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                }
            });
        } else
            return super.onOptionsItemSelected(item);
        return true;
    }
}
