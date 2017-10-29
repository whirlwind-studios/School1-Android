package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;
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

            final CollectionReference groupsReference = FirebaseFirestore.getInstance().collection("groups");
            final DocumentReference userReference = FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            final Group group = new Group();
            group.name = name;
            group.type = Group.TYPE_COURSE;

            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.get("schoolId") == null)
                        new TextPopup(R.string.error_title, "Youre not logged into a school").show();
                    else {
                        group.parentGroup = (String) documentSnapshot.get("schoolId");

                        groupsReference.add(group)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        group.setKey(documentReference.getId());

                                        userReference.collection("groups")
                                                .document(group.getKey())
                                                .set(true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        groupsReference
                                                                .document(group.parentGroup)
                                                                .collection("subGroups")
                                                                .document(group.getKey())
                                                                .set(true)
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
