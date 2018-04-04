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
import android.widget.RelativeLayout;

import com.example.kaihuynh.part_timejob.adapters.MyAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.interfaces.LoadMore;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobListFragment extends Fragment implements MyAdapter.ListItemClickListener {

    private MyAdapter mAdapter;
    private RecyclerView mJobRecyclerView;
    private ArrayList<Job> mJobArrayList;
    private RelativeLayout relativeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager scrollableLayoutManager = new LinearLayoutManager(getContext()){
        @Override
        public boolean canScrollVertically() {
            return true;
        }
    };

    private LinearLayoutManager cantScrollLayoutManager = new LinearLayoutManager(getContext()){
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    };

    private int scrollDist = 0;
    private boolean isVisible = true;
    private static final float MINIMUM = 25;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public JobListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        addComponents(view);
        initialize();
        setWidgetListener();
        return view;
    }

    private void addComponents(View view) {
        mJobRecyclerView = view.findViewById(R.id.rv_jobs);
        relativeLayout = view.findViewById(R.id.relative_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
    }


    private void initialize() {
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.lightBlue_700));

        mJobArrayList = JobManager.getInstance().getJobs();
        mJobRecyclerView.setLayoutManager(scrollableLayoutManager);
        mJobRecyclerView.setHasFixedSize(true);

        mAdapter = new MyAdapter(mJobRecyclerView, getContext(), R.layout.job_list_item, mJobArrayList, this);
        mJobRecyclerView.setAdapter(mAdapter);

        enableLoadMore();

    }

    private void enableLoadMore(){
        mAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                if(mJobArrayList.size() <= JobManager.getInstance().getJobCount() && mJobArrayList.size()>=10){
                    mJobArrayList.add(null);
                    mAdapter.notifyItemInserted(mJobArrayList.size()-1);
                    JobManager.getInstance().loadMoreJob(mJobArrayList.get(mJobArrayList.size()-2).getTimestamp());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mJobArrayList.get(mJobArrayList.size()-1) == null){
                                mJobArrayList.remove(mJobArrayList.size()-1);
                                mAdapter.notifyItemRemoved(mJobArrayList.size());
                            }
                            mJobArrayList.addAll(JobManager.getInstance().getLoadMoreJobs());
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setLoaded();
                        }
                    }, 2000);
                }
            }
        });
    }


    private void setWidgetListener() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mJobRecyclerView.setLayoutManager(cantScrollLayoutManager);
                JobManager.getInstance().refreshData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mJobArrayList = JobManager.getInstance().getJobs();
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        mJobRecyclerView.setLayoutManager(scrollableLayoutManager);
                    }
                }, 2000);
            }
        });

    }


    @Override
    public void onListItemClick(int clickItemIndex) {
        startActivity(new Intent(getContext(), JobDescriptionActivity.class));
    }
}
