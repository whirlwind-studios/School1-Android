package com.whirlwind.school1.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.whirlwind.school1.R;
import com.whirlwind.school1.base.BaseActivity;

public class LessonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_lesson_list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else
            return super.onOptionsItemSelected(item);
        return true;
    }
}
