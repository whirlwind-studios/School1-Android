package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.whirlwind.school1.R;
import com.whirlwind.school1.adapter.DashboardAdapter;
import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.model.Item;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private RecyclerView.Adapter adapter=new DashboardAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView=findViewById(R.id.activity_main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
