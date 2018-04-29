package com.example.kaihuynh.part_timejob;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.adapters.ApplyJobAdapter;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.ApplyJob;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobAppliedFragment extends Fragment implements ApplyJobAdapter.ListItemClickListener {
    private ApplyJobAdapter mAdapter, mEmployedAdapter, mUnemployedAdapter;
    private RecyclerView mViewingRecyclerView, mEmployedRecyclerView, mUnemployedRecyclerView;
    private ArrayList<ApplyJob> mViewingJobArrayList, mEmployedJobArrayList, mUnemployedJobArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout mEmptyView, viewingLayout, relativeViewing, employedLayout, relativeEmployed, unemployedLayout, relativeUnemployed;
    private View view1, view2, view3;
    private Button mShowViewing, mShowEmployed, mShowUnemployed;
    private TextView mQuantityViewingJob, mQuantityEmployedJob, mQuantityUnemployedJob;

    private static JobAppliedFragment sInstance = null;

    public JobAppliedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_applied, container, false);

        addComponents(view);
        initialize();
        setWidgetListeners();

        return view;
    }

    private void initialize() {
        sInstance = this;

        //loadData();

        mViewingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mViewingRecyclerView.setHasFixedSize(true);

        mEmployedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mEmployedRecyclerView.setHasFixedSize(true);

        mUnemployedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUnemployedRecyclerView.setHasFixedSize(true);

        mShowViewing.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.blue_down_narrow));
        mViewingRecyclerView.setVisibility(View.GONE);

        mShowEmployed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_down_narrow));
        mEmployedRecyclerView.setVisibility(View.GONE);

        mShowUnemployed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_down_narrow));
        mUnemployedRecyclerView.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.lightBlue_700));

        refreshData();

    }

    private void addComponents(View view) {
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        mViewingRecyclerView = view.findViewById(R.id.rv_viewing);
        swipeRefreshLayout = view.findViewById(R.id.sw_apply_jobs);
        mEmptyView = view.findViewById(R.id.rl_empty_apply_job);
        viewingLayout = view.findViewById(R.id.layout_viewing);
        relativeViewing = view.findViewById(R.id.rl_viewing);
        employedLayout = view.findViewById(R.id.layout_employed);
        relativeEmployed = view.findViewById(R.id.rl_employed);
        unemployedLayout = view.findViewById(R.id.layout_unemployed);
        relativeUnemployed = view.findViewById(R.id.rl_unemployed);
        mEmployedRecyclerView = view.findViewById(R.id.rv_employed);
        mUnemployedRecyclerView = view.findViewById(R.id.rv_unemployed);
        mShowViewing = view.findViewById(R.id.btn_show_viewing_job);
        mShowEmployed = view.findViewById(R.id.btn_show_employed_job);
        mShowUnemployed = view.findViewById(R.id.btn_show_unemployed_job);
        mQuantityViewingJob = view.findViewById(R.id.tv_quantity_viewing_job);
        mQuantityEmployedJob = view.findViewById(R.id.tv_quantity_employed_job);
        mQuantityUnemployedJob = view.findViewById(R.id.tv_quantity_unemployed_job);
    }

    private void setWidgetListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        swipeRefreshLayout.setRefreshing(false);
                        mViewingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
                            @Override
                            public boolean canScrollVertically() {
                                return true;
                            }
                        });
                    }
                }, 2000);
            }
        });

        relativeViewing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowViewing.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.blue_up_narrow).getConstantState())) {
                    mShowViewing.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.blue_down_narrow));
                    mViewingRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewingRecyclerView.clearAnimation();
                            mViewingRecyclerView.setVisibility(View.GONE);
                        }
                    }, 400);
                } else if (mShowViewing.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.blue_down_narrow).getConstantState())) {
                    mViewingRecyclerView.setVisibility(View.VISIBLE);
                    mShowViewing.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.blue_up_narrow));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewingRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                        }
                    }, -350);
                }
            }
        });

        relativeEmployed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowEmployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.green_up_narrow).getConstantState())) {
                    mShowEmployed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_down_narrow));
                    mEmployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmployedRecyclerView.clearAnimation();
                            mEmployedRecyclerView.setVisibility(View.GONE);
                        }
                    }, 400);
                } else if (mShowEmployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.green_down_narrow).getConstantState())) {
                    mEmployedRecyclerView.setVisibility(View.VISIBLE);
                    mEmployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mShowEmployed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_up_narrow));
                        }
                    }, -350);
                }
            }
        });

        relativeUnemployed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowUnemployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.red_up_narrow).getConstantState())) {
                    mShowUnemployed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_down_narrow));
                    mUnemployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mUnemployedRecyclerView.clearAnimation();
                            mUnemployedRecyclerView.setVisibility(View.GONE);
                        }
                    }, 400);
                } else if (mShowUnemployed.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.red_down_narrow).getConstantState())) {
                    mUnemployedRecyclerView.setVisibility(View.VISIBLE);
                    mUnemployedRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mShowUnemployed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_up_narrow));
                        }
                    }, -350);
                }
            }
        });
    }

    private void loadData() {
        mViewingJobArrayList = new ArrayList<>();
        mEmployedJobArrayList = new ArrayList<>();
        mUnemployedJobArrayList = new ArrayList<>();
        ArrayList<ApplyJob> list = new ArrayList<>();

        if (UserManager.getInstance().getUser().getAppliedJobList() != null) {
            list.addAll(UserManager.getInstance().getUser().getAppliedJobList());
        }

        for (ApplyJob job : list) {
            if (job.getStatus().equals(ApplyJob.EMPLOYED_STATUS)) {
                mEmployedJobArrayList.add(job);
            } else if (job.getStatus().equals(ApplyJob.UNEMPLOYED_STATUS)) {
                mUnemployedJobArrayList.add(job);
            }else {
                mViewingJobArrayList.add(job);
            }
        }
        mQuantityViewingJob.setText(String.valueOf(mViewingJobArrayList.size()));
        mQuantityEmployedJob.setText(String.valueOf(mEmployedJobArrayList.size()));
        mQuantityUnemployedJob.setText(String.valueOf(mUnemployedJobArrayList.size()));

        mAdapter = new ApplyJobAdapter(getContext(), R.layout.rv_job_item, mViewingJobArrayList, this);
        mViewingRecyclerView.setAdapter(mAdapter);

        mEmployedAdapter = new ApplyJobAdapter(getContext(), R.layout.rv_job_item, mEmployedJobArrayList, this);
        mEmployedRecyclerView.setAdapter(mEmployedAdapter);

        mUnemployedAdapter = new ApplyJobAdapter(getContext(), R.layout.rv_job_item, mUnemployedJobArrayList, this);
        mUnemployedRecyclerView.setAdapter(mUnemployedAdapter);
    }

    public void refreshData() {
        loadData();
        if (mViewingJobArrayList.size() < 1 && mEmployedJobArrayList.size()<1 && mUnemployedJobArrayList.size()<1) {
            mEmptyView.setVisibility(View.VISIBLE);
            viewingLayout.setVisibility(View.GONE);
            employedLayout.setVisibility(View.GONE);
            unemployedLayout.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            viewingLayout.setVisibility(View.VISIBLE);
            employedLayout.setVisibility(View.VISIBLE);
            unemployedLayout.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
        }
    }

    public static JobAppliedFragment getInstance() {
        if (sInstance == null) {
            sInstance = new JobAppliedFragment();
        }
        return sInstance;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onListItemClick(int clickItemIndex, ArrayList<ApplyJob> applyJobs) {
        Intent intent = new Intent(getContext(), JobDescriptionActivity.class);
        intent.putExtra("job", applyJobs.get(clickItemIndex).getJob());
        startActivity(intent);
    }
}
