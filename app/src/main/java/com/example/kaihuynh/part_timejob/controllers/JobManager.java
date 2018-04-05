package com.example.kaihuynh.part_timejob.controllers;

import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kai on 2018-02-03.
 */

public class JobManager {
    private final int NUMBER_DATA = 10;
    private static JobManager sInstance = null;
    private ArrayList<Job> mJobList;
    private ArrayList<Job> mLoadMoreList;
    private int jobCount = 0;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mJobRef;

    private JobManager() {
        this.mJobList = new ArrayList<>();
        this.mLoadMoreList = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mJobRef = mFirebaseDatabase.getReference().child("jobs");
    }

    public void loadData() {
        refreshData();

        mJobRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                jobCount = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void loadMoreJob(long timestamp) {
        Query query = mJobRef.orderByChild("timestamp").endAt(timestamp).limitToLast(NUMBER_DATA);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLoadMoreList.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Job job = noteDataSnapshot.getValue(Job.class);
                    mLoadMoreList.add(job);
                }
                mLoadMoreList.remove(mLoadMoreList.size() - 1);
                Collections.reverse(mLoadMoreList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void refreshData() {
        Query query = mJobRef.orderByChild("timestamp").limitToLast(NUMBER_DATA);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mJobList.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Job job = noteDataSnapshot.getValue(Job.class);
                    mJobList.add(job);
                }
                Collections.reverse(mJobList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void addJob(Job job) {
        String id = mJobRef.push().getKey();
        job.setId(id);
        mJobRef.child(id).setValue(job);
    }

    public ArrayList<Job> getJobs() {
        return this.mJobList;
    }

    public ArrayList<Job> getLoadMoreJobs() {
        return this.mLoadMoreList;
    }

    public Job getJobByRecruiterId(String id) {
        for (Job j : mJobList) {
            if (j.getId() == id) {
                return j;
            }
        }

        return null;
    }


    public ArrayList<Candidate> getCandidatesById(String id) {
        for (Job j : mJobList) {
            if (j.getId() == id) {
                return j.getCandidateList();
            }
        }

        return null;
    }

    public int getJobCount() {
        return this.jobCount;
    }

    public static JobManager getInstance() {
        if (sInstance == null) {
            sInstance = new JobManager();
        }

        return sInstance;
    }
}
