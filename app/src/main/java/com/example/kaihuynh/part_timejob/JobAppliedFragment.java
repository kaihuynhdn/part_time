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

import com.example.kaihuynh.part_timejob.adapters.JobAdapter;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobAppliedFragment extends Fragment implements JobAdapter.ListItemClickListener {

    private JobAdapter mAdapter;
    private RecyclerView mAppliedJobRecyclerView;
    private ArrayList<Job> mJobArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static JobAppliedFragment sInstance = null;

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
        setWigetListeners();
        return view;
    }

    private void initialize() {
        sInstance = this;

        mJobArrayList = new ArrayList<>();
        if (UserManager.getInstance().getUser().getAppliedJobList()!=null){
            mJobArrayList.addAll(UserManager.getInstance().getUser().getAppliedJobList());
        }
        mAdapter = new JobAdapter(getContext(), R.layout.job_list_item, mJobArrayList, this);
        mAppliedJobRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAppliedJobRecyclerView.setLayoutManager(layoutManager);
        mAppliedJobRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.lightBlue_700));
    }

    private void addComponents(View view) {
        mAppliedJobRecyclerView = view.findViewById(R.id.rv_applied_jobs);
        swipeRefreshLayout = view.findViewById(R.id.sw_apply_jobs);
    }

    private void setWigetListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAppliedJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mJobArrayList.clear();
                        if (UserManager.getInstance().getUser().getAppliedJobList()!=null){
                            mJobArrayList.addAll(UserManager.getInstance().getUser().getAppliedJobList());
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        mAppliedJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
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

    public void refreshData(){
        mJobArrayList = new ArrayList<>();
        if (UserManager.getInstance().getUser().getAppliedJobList()!=null){
            mJobArrayList.addAll(UserManager.getInstance().getUser().getAppliedJobList());
        }
        mAdapter = new JobAdapter(getContext(), R.layout.job_list_item, mJobArrayList, this);
        mAppliedJobRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        startActivity(new Intent(getContext(), JobDescriptionActivity.class));
    }

    public JobAdapter getAdapter() {
        return mAdapter;
    }


    public ArrayList<Job> getJobArrayList() {
        return mJobArrayList;
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
        refreshData();
    }
}
