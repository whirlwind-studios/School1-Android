package com.whirlwind.school1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.whirlwind.school1.models.Group;

import java.util.ArrayList;
import java.util.List;

public class CourseSelectionAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener, EventListener<QuerySnapshot> {

    private List<Group> courses = new ArrayList<>();
    private String groupId;
    private sharableListener sharableListener;

    public CourseSelectionAdapter() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("groups")
                .whereGreaterThan("access_level", Group.ACCESS_LEVEL_MEMBER)
                .addSnapshotListener(this);
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED: {
                    FirebaseFirestore.getInstance()
                            .collection("groups")
                            .document(change.getDocument().getId())
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                    if (!documentSnapshot.exists())
                                        return;

                                    Group course = documentSnapshot.toObject(Group.class);
                                    course.setId(documentSnapshot.getId());

                                    courses.add(course);
                                    notifyDataSetChanged();
                                    if (sharableListener != null)
                                        sharableListener.sharable(true);
                                }
                            });
                }
                break;

                case MODIFIED: {
                    FirebaseFirestore.getInstance()
                            .collection("groups")
                            .document(change.getDocument().getId())
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                    if (!documentSnapshot.exists())
                                        return;

                                    Group course = documentSnapshot.toObject(Group.class);
                                    course.setId(documentSnapshot.getId());

                                    for (int i = 0; i < courses.size(); i++)
                                        if (courses.get(i).getId().equals(course.getId())) {
                                            courses.set(i, course);
                                            notifyDataSetChanged();
                                            break;
                                        }
                                }
                            });
                }
                break;

                case REMOVED: {
                    String id = change.getDocument().getId();
                    for (int i = 0; i < courses.size(); i++)
                        if (courses.get(i).getId().equals(id)) {
                            courses.remove(i);
                            notifyDataSetChanged();
                            if (courses.size() == 0 && sharableListener != null)
                                sharableListener.sharable(false);
                            return;
                        }
                }
                break;
            }
        }
    }

    @Override
    public long getItemId(int i) {
        return courses.get(i).getId().hashCode();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        groupId = courses.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int i) {
        return courses.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        Group course = (Group) getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(course.name);

        return convertView;
    }

    public String getGroupId() {
        return groupId;
    }

    public int getItemPosition(String id) {
        for (int i = 0; i < courses.size(); i++)
            if (courses.get(i).getId().equals(id))
                return i;
        return 0;
    }

    public void setSharableListener(CourseSelectionAdapter.sharableListener sharableListener) {
        this.sharableListener = sharableListener;
    }

    public interface sharableListener {
        void sharable(boolean sharable);
    }
}