package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.models.Group;
import com.whirlwind.school1.popup.TextPopup;

import java.util.HashMap;
import java.util.Map;

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
            final DocumentReference userReference = FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists())
                        return;

                    if (documentSnapshot.get("school.id") == null)
                        new TextPopup(R.string.error_title, R.string.message_no_school).show();
                    else {
                        TextView textView = findViewById(R.id.activity_config_course_name);
                        String name = textView.getText().toString();

                        Group group = new Group();
                        group.name = name;
                        group.type = Group.TYPE_COURSE;
                        group.parentGroup = documentSnapshot.getString("school.id");

                        FirebaseFirestore.getInstance()
                                .collection("groups")
                                .add(group)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("access_level", Group.ACCESS_LEVEL_CREATOR);

                                        userReference.collection("groups")
                                                .document(documentReference.getId())
                                                .set(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        finish();
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
