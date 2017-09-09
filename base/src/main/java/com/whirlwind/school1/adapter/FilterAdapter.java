package com.whirlwind.school1.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterAdapter<T, E> extends ArrayAdapter<T> {

    private final List<T> objects;
    private final List<E> ownedObjects;
    private final Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<Sortable<T>> sortables = new ArrayList<>();
                for (T t : objects) {
                    int sortIndex = FilterAdapter.filter(constraint.toString(), String.valueOf(t));
                    if (sortIndex != -1)
                        sortables.add(new Sortable<T>(t, sortIndex) {
                            @Override
                            public int compareTo(@NonNull Sortable<T> o) {
                                int diff = o.sortIndex - sortIndex;
                                if (diff != 0)
                                    return diff;
                                else
                                    return String.valueOf(t).toLowerCase().compareTo(String.valueOf(t).toLowerCase());
                            }
                        });
                }
                Collections.sort(sortables);

                results.values = sortables;
                results.count = sortables.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                ArrayList<Sortable<T>> sortables = (ArrayList<Sortable<T>>) results.values;

                ArrayList<T> suggestions = new ArrayList<>(sortables.size());
                for (Sortable<T> sortable : sortables)
                    suggestions.add(sortable.t);

                addAll(suggestions);
            } else
                addAll(objects);

            notifyDataSetChanged();
        }
    };

    public FilterAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects, @NonNull List<E> ownedObjects) {
        super(context, resource, objects);
        this.objects = new ArrayList<>(objects);
        this.ownedObjects = ownedObjects;
    }

    public static int filter(String query, String name, String... args) {
        if (query.equals(""))
            return 0;
        else if (query.equals(name))
            return Integer.MAX_VALUE / 3;
        query = query.toLowerCase();
        int index = -1,
                length = query.length(),
                tolerance = (int) getTolerance(length);

        name = name.toLowerCase();
        if (name.contains(query))
            index += length * 4;
        else {
            StringBuilder s = new StringBuilder(name);
            for (int i = 0; i < length; i++) {
                char c = query.charAt(i);
                int position = indexOf(s, c);
                if (position == -1) {
                    if (tolerance == 0)
                        return -1;
                    else tolerance--;
                } else {
                    s.deleteCharAt(position);
                    index += 2;
                }
            }
        }
        index += tolerance;


        for (String string : args) {
            string = string.toLowerCase();
            if (string.contains(query))
                index += length * 2;
            else {
                StringBuilder s = new StringBuilder(string);
                for (int i = 0; i < length; i++) {
                    char c = query.charAt(i);
                    int position = indexOf(s, c);
                    if (position != -1) {
                        s.deleteCharAt(position);
                        index++;
                    }
                }
            }
        }
        return index;
    }

    private static int indexOf(StringBuilder builder, char c) {
        for (int i = 0; i < builder.length(); i++)
            if (builder.charAt(i) == c)
                return i;
        return -1;
    }

    private static float getTolerance(int length) {
        float tolerance = 0;
        for (int i = 0; i < length; i++) {
            tolerance += 3 / (i + 1);
        }
        tolerance /= 5;
        return tolerance;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    public static abstract class Sortable<T> implements Comparable<Sortable<T>> {
        public T t;
        public int sortIndex;

        public Sortable(T t, int sortIndex) {
            this.t = t;
            this.sortIndex = sortIndex;
        }
    }
}