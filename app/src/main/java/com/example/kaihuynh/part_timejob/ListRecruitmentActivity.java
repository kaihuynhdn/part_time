package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.kaihuynh.part_timejob.adapters.JobAdapter;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;

public class ListRecruitmentActivity extends AppCompatActivity implements JobAdapter.ListItemClickListener {

    private JobAdapter mAdapter;
    private RecyclerView mListRecruitmentRecyclerView;
    private ArrayList<Job> mJobArrayList;

    public static ListRecruitmentActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recruitment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addComponents();
        loadDataToRecyclerView();
    }

    private void loadDataToRecyclerView() {
        for (int i = 0; i<11; i++){
            Job job = new Job();
            job.setName("Job Title " + i);
            job.setSalary(String.valueOf(i));
            //job.setTimestamp(new Date().getTime());
            job.setLocation("Location " + i);

            mJobArrayList.add(job);
        }

        mAdapter = new JobAdapter(this, R.layout.job_list_item, mJobArrayList, this);
        mListRecruitmentRecyclerView.setAdapter(mAdapter);
    }

    private void addComponents() {
        sInstance=this;
        mListRecruitmentRecyclerView = findViewById(R.id.rv_list_recruitment);
        mJobArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListRecruitmentRecyclerView.setLayoutManager(layoutManager);
        mListRecruitmentRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        startActivity(new Intent(this, ListCandidateActivity.class));
    }

    public static ListRecruitmentActivity getInstance(){
        if (sInstance == null) {
            sInstance = new ListRecruitmentActivity();
        }
        return sInstance;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
