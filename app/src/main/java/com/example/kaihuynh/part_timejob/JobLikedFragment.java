package com.example.kaihuynh.part_timejob;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kaihuynh.part_timejob.adapters.JobAdapter;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobLikedFragment extends Fragment implements JobAdapter.ListItemClickListener{

    private JobAdapter mAdapter;
    private RecyclerView mLikedJobRecyclerView;
    private ArrayList<Job> mJobArrayList;

    public static JobLikedFragment sInstance = null;

    public JobLikedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job_liked, container, false);

        addComponents(view);
        loadDataToRecyclerView();
        return  view;
    }

    private void loadDataToRecyclerView() {
        for (int i = 0; i<11; i++){
            Job job = new Job();
            job.setName("Job Title " + i);
            job.setSalary(String.valueOf(i));
            job.setPostedDate(new Date());
            job.setLocation("Location " + i);

            mJobArrayList.add(job);
        }

        mAdapter = new JobAdapter(getContext(), R.layout.job_list_item, mJobArrayList, this);
        mLikedJobRecyclerView.setAdapter(mAdapter);
    }

    private void addComponents(View view) {
        sInstance = this;
        mLikedJobRecyclerView = view.findViewById(R.id.rv_liked_jobs);
        mJobArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mLikedJobRecyclerView.setLayoutManager(layoutManager);
        mLikedJobRecyclerView.setHasFixedSize(true);
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

    public static JobLikedFragment getInstance(){
        if (sInstance == null) {
            sInstance = new JobLikedFragment();
        }
        return sInstance;
    }
}
