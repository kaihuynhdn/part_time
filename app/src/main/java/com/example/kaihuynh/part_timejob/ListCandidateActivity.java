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

import com.example.kaihuynh.part_timejob.adapters.CandidateAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;

public class ListCandidateActivity extends AppCompatActivity implements CandidateAdapter.ListItemClickListener{

    private CandidateAdapter mAdapter;
    private RecyclerView mListCandidateRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Candidate> mCandidateList;

    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_candidate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addComponents();
        initialize();
        setWigetListeners();
    }

    private void initialize() {
        Intent intent = getIntent();
        job = (Job) intent.getSerializableExtra("job");
        mCandidateList = new ArrayList<>();
        if(job.getCandidateList()!=null){
            mCandidateList.addAll(job.getCandidateList());
        }
        mAdapter = new CandidateAdapter(this, R.layout.candidate_rv_item, mCandidateList, this);
        mListCandidateRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListCandidateRecyclerView.setLayoutManager(layoutManager);
        mListCandidateRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(ListCandidateActivity.this, R.color.lightBlue_700));
    }

    private void addComponents() {
        mListCandidateRecyclerView = findViewById(R.id.rv_list_candidate);
        swipeRefreshLayout = findViewById(R.id.sw_list_candidate);
    }

    private void setWigetListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListCandidateRecyclerView.setLayoutManager(new LinearLayoutManager(ListCandidateActivity.this){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                JobManager.getInstance().loadCandidateList(job.getId());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCandidateList = JobManager.getInstance().getCandidateList();
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        mListCandidateRecyclerView.setLayoutManager(new LinearLayoutManager(ListCandidateActivity.this){
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
        Intent intent = new Intent(ListCandidateActivity.this, CandidateActivity.class);
        intent.putExtra("candidate", mCandidateList.get(clickItemIndex));
        startActivity(intent);
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
