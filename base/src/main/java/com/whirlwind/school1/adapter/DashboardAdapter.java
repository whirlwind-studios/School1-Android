package com.whirlwind.school1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.whirlwind.school1.R;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.helper.DateHelper;
import com.whirlwind.school1.models.Item;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    private static final int VIEW_TYPE_HEADER = 0, VIEW_TYPE_ITEM = 1;

    private final List<Object> rowItems = new ArrayList<>();

    public DashboardAdapter(Context context) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        // Today is default
        rowItems.add(new Section(context.getString(R.string.section_title_today), getSectionDate(calendar)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        if (today >= Calendar.MONDAY && today <= Calendar.WEDNESDAY) {
            rowItems.add(new Section(context.getString(R.string.section_title_tomorrow), getSectionDate(calendar)));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            rowItems.add(new Section(context.getString(R.string.section_title_this_week), getSectionDate(calendar)));
        } else if (today == Calendar.THURSDAY) {
            rowItems.add(new Section(context.getString(R.string.section_title_tomorrow), getSectionDate(calendar)));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            rowItems.add(new Section(context.getString(R.string.section_title_weekend), getSectionDate(calendar)));
        } else if (today == Calendar.FRIDAY)
            rowItems.add(new Section(context.getString(R.string.section_title_weekend), getSectionDate(calendar)));
        else if (today == Calendar.SATURDAY)
            rowItems.add(new Section(context.getString(R.string.section_title_tomorrow), getSectionDate(calendar)));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if (calendar.getFirstDayOfWeek() != Calendar.SUNDAY || today != Calendar.SUNDAY)
            calendar.add(Calendar.WEEK_OF_YEAR, 1);

        rowItems.add(new Section(context.getString(R.string.section_title_next_week), getSectionDate(calendar)));

        // TODO: Holiday and interval sections (between two holidays, to give a clearer overview over whats happening in the long run)
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        rowItems.add(new Section("Until the end of the universe", getSectionDate(calendar)));

        // TODO: Listen on login
        UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
        if (userInfo != null) {
            DocumentReference userReference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userInfo.getUid());

            userReference.collection("groups")
                    .addSnapshotListener(this);
            userReference.collection("items")
                    .addSnapshotListener(new GroupItemsChangeListener(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        }
    }

    private static long getSectionDate(Calendar calendar) {
        return DateHelper.getDate(calendar, DateHelper.START);
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED: {
                    String groupId = change.getDocument().toObject(String.class);
                    FirebaseFirestore.getInstance()
                            .collection("groups")
                            .document(groupId)
                            .collection("items")
                            .addSnapshotListener(new GroupItemsChangeListener(groupId));
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (rowItems.get(position) instanceof Section)
            return VIEW_TYPE_HEADER;
        else return VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resId = viewType == VIEW_TYPE_HEADER ? R.layout.row_layout_dashboard_header : R.layout.row_layout_dashboard_item;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object object = rowItems.get(position);
        if (object instanceof RowItem)
            ((RowItem) object).populate(holder.itemView, position);
    }

    public interface RowItem {
        long getDate();

        void setDate(long date);
        void populate(View view, int position);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view, int viewType) {
            super(view);
            if (viewType != VIEW_TYPE_HEADER && viewType != VIEW_TYPE_ITEM)
                throw new InvalidParameterException("viewType must be VIEW_TYPE_HEADER or VIEW_TYPE_ITEM");
        }
    }

    private static class Section implements RowItem, BackendHelper.Queryable {
        private String header;
        private long date;

        private Section(String header, long date) {
            this.header = header;
            setDate(date);
        }

        @Override
        public long getDate() {
            return date;
        }

        @Override
        public void setDate(long date) {
            this.date = date;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public void setId(String id) {
        }

        @Override
        public void populate(View view, int position) {
            TextView sectionHeader = view.findViewById(R.id.row_layout_dashboard_header);
            sectionHeader.setText(header);
            View divider = view.findViewById(R.id.row_layout_dashboard_divider);
            if (position == 0)
                divider.setVisibility(View.GONE);
            else
                divider.setVisibility(View.VISIBLE);
        }
    }

    private class GroupItemsChangeListener implements EventListener<QuerySnapshot> {

        private final String groupId;

        private GroupItemsChangeListener(String groupId) {
            this.groupId = groupId;
        }

        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                Item item = change.getDocument().toObject(Item.class);
                item.setId(change.getDocument().getId());
                switch (change.getType()) {
                    case ADDED:
                        onChildAdded(item);
                        break;
                    case MODIFIED:
                        onChildRemoved(item);
                        onChildAdded(item);
                        break;
                    case REMOVED:
                        onChildRemoved(item);
                        break;
                }
            }
        }

        private void onChildAdded(Item item) {
            item.setParent(groupId);

            for (int i = rowItems.size(); i > 0; i--) {
                Object object = rowItems.get(i - 1);
                if (object instanceof RowItem)
                    if (item.getDate() >= ((RowItem) object).getDate()) {
                        rowItems.add(i, item);
                        break;
                    }
            }
            notifyDataSetChanged();

        }

        private void onChildRemoved(Item item) {
            for (int i = 0; i < rowItems.size(); i++) {
                Object object = rowItems.get(i);
                if (object instanceof BackendHelper.Queryable)
                    if (item.getId().equals(((BackendHelper.Queryable) object).getId())) {
                        rowItems.remove(i);
                        break;
                    }
            }
            notifyDataSetChanged();
        }
    }
}