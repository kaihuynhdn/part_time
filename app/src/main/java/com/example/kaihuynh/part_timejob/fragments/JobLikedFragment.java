package com.example.kaihuynh.part_timejob.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.kaihuynh.part_timejob.JobDescriptionActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.adapters.JobAdapter;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobLikedFragment extends Fragment implements JobAdapter.ListItemClickListener{
    @SuppressLint("StaticFieldLeak")
    private static JobLikedFragment sInstance = null;
    private JobAdapter mAdapter;
    private RecyclerView mLikedJobRecyclerView;
    private ArrayList<Job> mJobArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout mEmptyView;


    public JobLikedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job_liked, container, false);

        addComponents(view);
        initialize();
        setWidgetsListener();


        return  view;
    }

    private void initialize() {
        sInstance = this;

        mJobArrayList = new ArrayList<>();
        if (UserManager.getInstance().getUser().getFavouriteJobList()!=null){
            mJobArrayList.addAll(UserManager.getInstance().getUser().getFavouriteJobList());
        }
        mAdapter = new JobAdapter(getContext(), R.layout.rv_job_item, mJobArrayList, this);
        mLikedJobRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mLikedJobRecyclerView.setLayoutManager(layoutManager);
        mLikedJobRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.lightBlue_700));

        refreshData();

    }

    private void addComponents(View view) {
        mLikedJobRecyclerView = view.findViewById(R.id.rv_liked_jobs);
        swipeRefreshLayout = view.findViewById(R.id.sw_like_jobs);
        mEmptyView = view.findViewById(R.id.rl_empty_like_job);

    }

    private void setWidgetsListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLikedJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
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
                        mLikedJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
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
        if (UserManager.getInstance().getUser().getFavouriteJobList()!=null){
            mJobArrayList.addAll(UserManager.getInstance().getUser().getFavouriteJobList());
        }
        if (mJobArrayList.size()<1){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
        mAdapter = new JobAdapter(getContext(), R.layout.rv_job_item, mJobArrayList, this);
        mLikedJobRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent intent = new Intent(getContext(), JobDescriptionActivity.class);
        intent.putExtra("job", mJobArrayList.get(clickItemIndex));
        startActivity(intent);
    }

    public static JobLikedFragment getInstance(){
        if (sInstance == null) {
            sInstance = new JobLikedFragment();
        }
        return sInstance;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
