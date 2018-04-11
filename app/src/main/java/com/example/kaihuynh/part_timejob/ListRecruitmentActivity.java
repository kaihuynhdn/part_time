package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.kaihuynh.part_timejob.adapters.JobAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;

public class ListRecruitmentActivity extends AppCompatActivity implements JobAdapter.ListItemClickListener {

    private JobAdapter mAdapter;
    private RecyclerView mListRecruitmentRecyclerView;
    private ArrayList<Job> mJobArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static ListRecruitmentActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recruitment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addComponents();
        initialize();
        setWigetListeners();
    }

    private void addComponents() {
        mListRecruitmentRecyclerView = findViewById(R.id.rv_list_recruitment);
        swipeRefreshLayout = findViewById(R.id.sw_list_recruitment);
    }

    private void initialize() {
        sInstance=this;
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(ListRecruitmentActivity.this, R.color.lightBlue_700));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListRecruitmentRecyclerView.setLayoutManager(layoutManager);
        mListRecruitmentRecyclerView.setHasFixedSize(true);

        mJobArrayList = new ArrayList<>();
        mJobArrayList.addAll(JobManager.getInstance().getJobListByUser());
        mAdapter = new JobAdapter(ListRecruitmentActivity.this, R.layout.job_list_item, mJobArrayList, this);
        mListRecruitmentRecyclerView.setAdapter(mAdapter);
    }


    private void setWigetListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListRecruitmentRecyclerView.setLayoutManager(new LinearLayoutManager(ListRecruitmentActivity.this){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                JobManager.getInstance().loadJobByUser(UserManager.getInstance().getUser().getId());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mJobArrayList = JobManager.getInstance().getJobListByUser();
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        mListRecruitmentRecyclerView.setLayoutManager(new LinearLayoutManager(ListRecruitmentActivity.this){
                            @Override
                            public boolean canScrollVertically() {
                                return true;
                            }
                        });
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent intent = new Intent(this, ListCandidateActivity.class);
        intent.putExtra("job", mJobArrayList.get(clickItemIndex));
        startActivity(intent);
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
