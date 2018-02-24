package com.example.kaihuynh.part_timejob;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TabHost;

public class JobDescriptionActivity extends AppCompatActivity {

    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addComponents();
        initial();
    }

    private void initial() {
        mTabHost.setup();
        TabHost.TabSpec mJobDetailTabSpec = mTabHost.newTabSpec("t1");
        mJobDetailTabSpec.setIndicator("CÔNG VIỆC");
        mJobDetailTabSpec.setContent(R.id.tab1);
        mTabHost.addTab(mJobDetailTabSpec);

        TabHost.TabSpec mRecruiterDetailTabSpec = mTabHost.newTabSpec("t2");
        mRecruiterDetailTabSpec.setIndicator("NGƯỜI TUYỂN");
        mRecruiterDetailTabSpec.setContent(R.id.tab2);
        mTabHost.addTab(mRecruiterDetailTabSpec);
    }

    private void addComponents() {
        mTabHost = findViewById(R.id.tabHost);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
