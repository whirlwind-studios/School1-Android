package com.whirlwind.school1.adapter;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.model.Item;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0, VIEW_TYPE_ITEM = 1;

    private final List<RowItem> rowItems = new ArrayList<>();

    public DashboardAdapter() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (today >= Calendar.MONDAY && today <= Calendar.WEDNESDAY) {
            rowItems.add(new Section("Today"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
            rowItems.add(new Section("Tomorrow"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
            rowItems.add(new Section("This week"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
        } else if (today == Calendar.THURSDAY) {
            rowItems.add(new Section("Today"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
            rowItems.add(new Section("Tomorrow"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
            rowItems.add(new Section("Weekend"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
        } else if (today == Calendar.FRIDAY) {
            rowItems.add(new Section("Today"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
            rowItems.add(new Section("Weekend"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
        } else if (today == Calendar.SATURDAY) {
            rowItems.add(new Section("Weekend"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
        } else if (today == Calendar.SUNDAY) {
            rowItems.add(new Section("Today"));
            rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));

        }

        rowItems.add(new Section("Next week"));
        rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
        rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));

        // TODO: Holiday and interval sections (between two holidays, to give a clearer overview over whats happening in the long run)
        rowItems.add(new Section("Until the end of the universe"));
        rowItems.add(new Item("LOL", "It's quite empty here...", "Today"));
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
        rowItems.get(position).populate(holder.itemView);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view, int viewType) {
            super(view);
            if (viewType != VIEW_TYPE_HEADER && viewType != VIEW_TYPE_ITEM)
                throw new InvalidParameterException("viewType must be VIEW_TYPE_HEADER or VIEW_TYPE_ITEM");
        }
    }

    public interface RowItem {
        void populate(View view);
    }

    private static class Section implements RowItem {
        private String header;

        private Section(String header) {
            this.header = header;
        }

        @Override
        public void populate(View view) {
            TextView sectionHeader = (TextView) view;
            sectionHeader.setText(header);
        }
    }
}