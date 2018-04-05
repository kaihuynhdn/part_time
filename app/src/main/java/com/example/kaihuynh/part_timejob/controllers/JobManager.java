package com.example.kaihuynh.part_timejob.controllers;

import android.support.annotation.NonNull;

import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Kai on 2018-02-03.
 */

public class JobManager {
    private final int NUMBER_DATA = 10;
    private static JobManager sInstance = null;
    private ArrayList<Job> mJobList;
    private ArrayList<Job> mLoadMoreList;
    private int jobCount = 0;

    //    private FirebaseDatabase mFirebaseDatabase;
//    private DatabaseReference mJobRef;
    private CollectionReference mJobReference;


    private JobManager() {
        this.mJobList = new ArrayList<>();
        this.mLoadMoreList = new ArrayList<>();
        mJobReference = FirebaseFirestore.getInstance().collection("jobs");
    }

    public void loadData() {
        refreshData();
        mJobReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                jobCount = (int) documentSnapshots.getDocuments().size();
            }
        });
    }

    public void loadMoreJob(long timestamp) {
        mJobReference.orderBy("timestamp", Direction.DESCENDING).startAt(timestamp).limit(NUMBER_DATA).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mLoadMoreList.clear();
                            for (DocumentSnapshot d : task.getResult()) {
                                Job job = d.toObject(Job.class);
                                mLoadMoreList.add(job);
                            }
                            mLoadMoreList.remove(mLoadMoreList.size()-1);
                        }
                    }
                });

//        Query query = mJobRef.orderByChild("timestamp").endAt(timestamp).limitToLast(NUMBER_DATA);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mLoadMoreList.clear();
//                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
//                    Job job = noteDataSnapshot.getValue(Job.class);
//                    mLoadMoreList.add(job);
//                }
//                mLoadMoreList.remove(mLoadMoreList.size() - 1);
//                Collections.reverse(mLoadMoreList);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void refreshData() {
        mJobReference.orderBy("timestamp", Direction.DESCENDING).limit(NUMBER_DATA).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            mJobList.clear();
                            for (DocumentSnapshot d : task.getResult()) {
                                Job job = d.toObject(Job.class);
                                mJobList.add(job);
                            }
                        }
                    }
                });


//        Query query = mJobRef.orderByChild("timestamp").limitToLast(NUMBER_DATA);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mJobList.clear();
//                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
//                    Job job = noteDataSnapshot.getValue(Job.class);
//                    mJobList.add(job);
//                }
//                Collections.reverse(mJobList);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


    public void addJob(Job job) {
        String id = mJobReference.document().getId();
        job.setId(id);
        mJobReference.document(id).set(job);
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
