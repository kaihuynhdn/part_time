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
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.adapters.CandidateAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.ApplyJob;

import java.util.ArrayList;

public class ListCandidateActivity extends AppCompatActivity implements CandidateAdapter.ListItemClickListener{

    private CandidateAdapter mAdapter, mEmployedAdapter, mUnemployedAdapter;
    private RecyclerView mListCandidateRecyclerView, mEmployedRecyclerView, mUnemployedRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Candidate> mCandidateList, mEmployedJobArrayList, mUnemployedJobArrayList;
    private RelativeLayout mEmptyView, waitingLayout, relativeViewing, employedLayout, relativeEmployed, unemployedLayout, relativeUnemployed;
    private View view1, view2, view3;
    private Button mShowWaiting, mShowEmployed, mShowUnemployed;
    private TextView mQuantityWaitingJob, mQuantityEmployedJob, mQuantityUnemployedJob;
    private ScrollView scrollView;

    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_candidate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addComponents();
        initialize();
        setWidgetListeners();
    }

    private void initialize() {
        Intent intent = getIntent();
        job = (Job) intent.getSerializableExtra("job");
        getSupportActionBar().setTitle(job.getName());
        JobManager.getInstance().loadJobById(job.getId());
        JobManager.getInstance().loadCandidateList(job.getId());

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(ListCandidateActivity.this, R.color.lightBlue_700));
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                while (!JobManager.isLoadCandidateList){

                }
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);

        mListCandidateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListCandidateRecyclerView.setHasFixedSize(true);

        mEmployedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEmployedRecyclerView.setHasFixedSize(true);

        mUnemployedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUnemployedRecyclerView.setHasFixedSize(true);

        mShowWaiting.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_down_narrow));
        mListCandidateRecyclerView.setVisibility(View.GONE);

        mShowEmployed.setBackground(ContextCompat.getDrawable(this, R.drawable.green_down_narrow));
        mEmployedRecyclerView.setVisibility(View.GONE);

        mShowUnemployed.setBackground(ContextCompat.getDrawable(this, R.drawable.red_down_narrow));
        mUnemployedRecyclerView.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.lightBlue_700));
    }

    private void addComponents() {
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        mListCandidateRecyclerView = findViewById(R.id.rv_list_candidate);
        swipeRefreshLayout = findViewById(R.id.sw_list_candidate);
        mEmptyView = findViewById(R.id.rl_empty_candidate);
        waitingLayout = findViewById(R.id.layout_waiting);
        relativeViewing = findViewById(R.id.rl_waiting);
        employedLayout = findViewById(R.id.layout_employed);
        relativeEmployed = findViewById(R.id.rl_employed);
        unemployedLayout = findViewById(R.id.layout_unemployed);
        relativeUnemployed = findViewById(R.id.rl_unemployed);
        mEmployedRecyclerView = findViewById(R.id.rv_employed);
        mUnemployedRecyclerView = findViewById(R.id.rv_unemployed);
        mShowWaiting = findViewById(R.id.btn_show_waiting_job);
        mShowEmployed = findViewById(R.id.btn_show_employed_job);
        mShowUnemployed = findViewById(R.id.btn_show_unemployed_job);
        mQuantityWaitingJob = findViewById(R.id.tv_quantity_waiting_job);
        mQuantityEmployedJob = findViewById(R.id.tv_quantity_employed_job);
        mQuantityUnemployedJob = findViewById(R.id.tv_quantity_unemployed_job);
        scrollView = findViewById(R.id.scrollView);
    }

    private void setWidgetListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListCandidateRecyclerView.setLayoutManager(new LinearLayoutManager(ListCandidateActivity.this){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                JobManager.getInstance().loadJobById(job.getId());
                JobManager.getInstance().loadCandidateList(job.getId());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        while (!JobManager.isLoadCandidateList){

                        }
                        refreshData();
                        swipeRefreshLayout.setRefreshing(false);
                        mListCandidateRecyclerView.setLayoutManager(new LinearLayoutManager(ListCandidateActivity.this));
                    }
                }, 2000);
            }
        });

        relativeViewing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowWaiting.getBackground().getConstantState().equals(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.blue_up_narrow).getConstantState())) {
                    mShowWaiting.setBackground(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.blue_down_narrow));
                    mListCandidateRecyclerView.startAnimation(AnimationUtils.loadAnimation(ListCandidateActivity.this, R.anim.slide_up));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListCandidateRecyclerView.clearAnimation();
                            mListCandidateRecyclerView.setVisibility(View.GONE);
                        }
                    }, 400);
                } else if (mShowWaiting.getBackground().getConstantState().equals(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.blue_down_narrow).getConstantState())) {
                    mListCandidateRecyclerView.setVisibility(View.VISIBLE);
                    mShowWaiting.setBackground(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.blue_up_narrow));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListCandidateRecyclerView.startAnimation(AnimationUtils.loadAnimation(ListCandidateActivity.this, R.anim.slide_down));
                        }
                    }, -350);
                }
            }
        });

        relativeEmployed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowEmployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.green_up_narrow).getConstantState())) {
                    mShowEmployed.setBackground(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.green_down_narrow));
                    mEmployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(ListCandidateActivity.this, R.anim.slide_up));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmployedRecyclerView.clearAnimation();
                            mEmployedRecyclerView.setVisibility(View.GONE);
                        }
                    }, 400);
                } else if (mShowEmployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.green_down_narrow).getConstantState())) {
                    mEmployedRecyclerView.setVisibility(View.VISIBLE);
                    mEmployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(ListCandidateActivity.this, R.anim.slide_down));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mShowEmployed.setBackground(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.green_up_narrow));
                        }
                    }, -350);
                }
            }
        });

        relativeUnemployed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowUnemployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.red_up_narrow).getConstantState())) {
                    mShowUnemployed.setBackground(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.red_down_narrow));
                    mUnemployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(ListCandidateActivity.this, R.anim.slide_up));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mUnemployedRecyclerView.clearAnimation();
                            mUnemployedRecyclerView.setVisibility(View.GONE);
                        }
                    }, 400);
                } else if (mShowUnemployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.red_down_narrow).getConstantState())) {
                    mUnemployedRecyclerView.setVisibility(View.VISIBLE);
                    mUnemployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(ListCandidateActivity.this, R.anim.slide_down));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mShowUnemployed.setBackground(ContextCompat.getDrawable(ListCandidateActivity.this, R.drawable.red_up_narrow));
                        }
                    }, -350);
                }
            }
        });
    }

    private void loadData() {
        mCandidateList = new ArrayList<>();
        mEmployedJobArrayList = new ArrayList<>();
        mUnemployedJobArrayList = new ArrayList<>();

        if (JobManager.getInstance().getCandidateList() != null) {
            mCandidateList.addAll(JobManager.getInstance().getCandidateList());
        }

        for (Candidate c : mCandidateList) {
            if (c.getStatus().equals(ApplyJob.EMPLOYED_STATUS)) {
                mEmployedJobArrayList.add(c);
                mCandidateList.remove(c);
            } else if (c.getStatus().equals(ApplyJob.UNEMPLOYED_STATUS)) {
                mUnemployedJobArrayList.add(c);
                mCandidateList.remove(c);
            }
        }

        mQuantityWaitingJob.setText(String.valueOf(mCandidateList.size()+""));
        mQuantityEmployedJob.setText(String.valueOf(mEmployedJobArrayList.size()+""));
        mQuantityUnemployedJob.setText(String.valueOf(mUnemployedJobArrayList.size()+""));

        mAdapter = new CandidateAdapter(ListCandidateActivity.this, R.layout.candidate_rv_item, mCandidateList, this);
        mListCandidateRecyclerView.setAdapter(mAdapter);

        mEmployedAdapter = new CandidateAdapter(this, R.layout.candidate_rv_item, mEmployedJobArrayList, this);
        mEmployedRecyclerView.setAdapter(mEmployedAdapter);

        mUnemployedAdapter = new CandidateAdapter(this, R.layout.candidate_rv_item, mUnemployedJobArrayList, this);
        mUnemployedRecyclerView.setAdapter(mUnemployedAdapter);
    }

    public void refreshData() {
        loadData();
        if (mCandidateList.size() < 1 && mEmployedJobArrayList.size()<1 && mUnemployedJobArrayList.size()<1) {
            mEmptyView.setVisibility(View.VISIBLE);
            waitingLayout.setVisibility(View.GONE);
            employedLayout.setVisibility(View.GONE);
            unemployedLayout.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            waitingLayout.setVisibility(View.VISIBLE);
            employedLayout.setVisibility(View.VISIBLE);
            unemployedLayout.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        JobManager.getInstance().loadCandidateList(job.getId());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                while (!JobManager.isLoadCandidateList){

                }
                refreshData();
            }
        }, 1000);
    }

    @Override
    public void onListItemClick(int clickItemIndex, ArrayList<Candidate> mCandidates) {
        Intent intent = new Intent(ListCandidateActivity.this, CandidateActivity.class);
        intent.putExtra("candidate", mCandidates.get(clickItemIndex));
        intent.putExtra("job", JobManager.getInstance().getJobById());
        while (!JobManager.isLoadJobById){

        }
        startActivity(intent);
    }
}
