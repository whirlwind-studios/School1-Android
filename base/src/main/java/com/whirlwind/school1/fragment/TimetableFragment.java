package com.whirlwind.school1.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.whirlwind.school1.R;

public class TimetableFragment extends Fragment {

    //private final ArrayList<Lesson> changes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GridView view = (GridView) inflater.inflate(R.layout.fragment_timetable, container, false);

        /*if (savedInstanceState != null) {
            long[] ids = savedInstanceState.getLongArray("ids");
            String[] names = savedInstanceState.getStringArray("names");
            byte[] times = savedInstanceState.getByteArray("times");

            if (ids != null && names != null && times != null) {
                changes.clear();
                for (int i = 0; i < ids.length; i++)
                    changes.add(new Lesson(ids[i], names[i], times[i], Codes.private_));
            }
        }
        view.setAdapter(new TimetableConfigAdapter(-1, changes));*/
        return view;
    }

    @Override
    public void onPause() {
        //dataInterface.configLessons(0, changes, null);
        //changes.clear();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /*if (!changes.isEmpty()) {
            long[] ids = new long[changes.size()];
            String[] names = new String[changes.size()];
            byte[] times = new byte[changes.size()];

            for (int i = 0; i < changes.size(); i++) {
                Lesson l = changes.get(i);
                ids[i] = l.id;
                names[i] = l.name;
                times[i] = l.time;
            }

            outState.putLongArray("ids", ids);
            outState.putStringArray("names", names);
            outState.putByteArray("times", times);
        }*/
    }
}
