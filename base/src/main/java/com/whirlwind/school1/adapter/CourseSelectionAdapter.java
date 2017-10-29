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

public class CourseSelectionAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {

    private List<Group> courses = new ArrayList<>();
    private String groupId;
    private sharableListener sharableListener;

    public CourseSelectionAdapter() {
        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("groups")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (documentSnapshots == null)
                            return;

                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            DocumentSnapshot snapshot = change.getDocument();

                            DocumentChange.Type type = change.getType();
                            switch (type) {
                                case MODIFIED: {
                                    Boolean bool = snapshot.toObject(Boolean.class);
                                    if (bool)
                                        type = DocumentChange.Type.ADDED;
                                    else
                                        type = DocumentChange.Type.REMOVED;
                                }
                                case ADDED: {
                                    Boolean bool = snapshot.toObject(Boolean.class);
                                    if (!bool)
                                        return;

                                    FirebaseFirestore.getInstance()
                                            .collection("groups")
                                            .document(snapshot.getId())
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                    Group course = documentSnapshot.toObject(Group.class);
                                                    course.setKey(documentSnapshot.getId());
                                                    for (int i = 0; i < courses.size(); i++)
                                                        if (courses.get(i).getKey().equals(course.getKey())) {
                                                            courses.set(i, course);
                                                            notifyDataSetChanged();
                                                            return;
                                                        }

                                                    // Not already included, just added
                                                    courses.add(course);
                                                    notifyDataSetChanged();
                                                    if (sharableListener != null)
                                                        sharableListener.sharable(true);
                                                }
                                            });
                                }
                                break;

                                case REMOVED: {
                                    for (int i = 0; i < courses.size(); i++)
                                        if (courses.get(i).getKey().equals(change.getDocument().getId())) {
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
                });
    }

    @Override
    public long getItemId(int i) {
        return courses.get(i).getKey().hashCode();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        groupId = courses.get(position).getKey();
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

    public void setSharableListener(CourseSelectionAdapter.sharableListener sharableListener) {
        this.sharableListener = sharableListener;
    }

    public interface sharableListener {
        void sharable(boolean sharable);
    }
}
