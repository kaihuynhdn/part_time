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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Kai on 2018-02-03.
 */

public class JobManager {
    private final int NUMBER_DATA = 10;
    private static JobManager sInstance = null;
    private ArrayList<Candidate> mCandidateList;
    private ArrayList<Job> mJobList;
    private ArrayList<Job> mLoadMoreList;
    private ArrayList<Job> mJobListByLocation;
    private ArrayList<Job> mMoreJobListByLocation;
    private Job jobById;
    private int jobQuantity = 0;
    private ListenerRegistration listenerRegistration;
    public static boolean isRefreshed = false;
    public static boolean isLoadJobById = false;
    public static boolean isLoadJobByLoaction = false;
    public static boolean isLoadCandidateList = false;
    public static boolean isLoadMoreJob = false;
    public static boolean isLoadMoreJobByLocation = false;
    public static boolean isLoadedJobQuantity = false;

    private CollectionReference mJobReference;


    private JobManager() {
        this.mJobList = new ArrayList<>();
        this.mLoadMoreList = new ArrayList<>();
        this.mCandidateList = new ArrayList<>();
        this.mJobListByLocation = new ArrayList<>();
        this.mMoreJobListByLocation = new ArrayList<>();
        mJobReference = FirebaseFirestore.getInstance().collection("jobs");
    }

    public void loadData() {
        isLoadedJobQuantity = false;
        listenerRegistration = mJobReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                jobQuantity = documentSnapshots.getDocuments().size();
                isLoadedJobQuantity = true;
            }
        });
    }

    public void loadMoreJob(long timestamp) {
        isLoadMoreJob = false;
        mJobReference.orderBy("timestamp", Direction.DESCENDING).startAt(timestamp).limit(jobQuantity == 0 ? 1 : jobQuantity).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mLoadMoreList.clear();
                            for (DocumentSnapshot d : task.getResult()) {
                                Job job = d.toObject(Job.class);
                                if (job.getStatus().equals("Đang tuyển")) {
                                    mLoadMoreList.add(job);
                                }
                                if (mLoadMoreList.size() == NUMBER_DATA) {
                                    break;
                                }
                            }
                            if (mLoadMoreList.size() > 0) {
                                mLoadMoreList.remove(0);
                            }
                            isLoadMoreJob = true;
                        }
                    }
                });
    }

    public void refreshData() {
        isRefreshed = false;
        mJobReference.orderBy("timestamp", Direction.DESCENDING).limit(jobQuantity == 0 ? 1 : jobQuantity).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mJobList.clear();
                            for (DocumentSnapshot d : task.getResult()) {
                                Job job = d.toObject(Job.class);
                                if (job.getStatus().equals("Đang tuyển")) {
                                    mJobList.add(job);
                                }
                                if (mJobList.size() == NUMBER_DATA) {
                                    break;
                                }
                            }
                            isRefreshed = true;
                        }
                    }
                });
    }


    public void loadJobById(String id) {
        jobById = null;
        isLoadJobById = false;
        mJobReference.whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DocumentSnapshot d : task.getResult()) {
                        jobById = d.toObject(Job.class);
                    }
                    isLoadJobById = true;
                }
            }
        });
    }

    public void updateJob(Job job) {
        mJobReference.document(job.getId()).set(job);
    }

    public void loadCandidateList(String id) {
        isLoadCandidateList = false;
        mJobReference.whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mCandidateList.clear();
                    for (DocumentSnapshot d : task.getResult()) {
                        Job job = d.toObject(Job.class);
                        mCandidateList.addAll(job.getCandidateList());
                    }
                    isLoadCandidateList = true;
                }
            }
        });
    }

    public void loadJobByLocation(final String location) {
        isLoadJobByLoaction = false;
        mJobReference.orderBy("timestamp", Direction.DESCENDING).limit(jobQuantity == 0 ? 1 : jobQuantity).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mJobListByLocation.clear();
                            for (DocumentSnapshot d : task.getResult()) {
                                Job job = d.toObject(Job.class);
                                if (job.getStatus().equals("Đang tuyển") && job.getLocation().equals(location)) {
                                    mJobListByLocation.add(job);
                                }
                                if (mJobListByLocation.size() == NUMBER_DATA) {
                                    break;
                                }
                            }
                            isLoadJobByLoaction = true;
                        }
                    }
                });
    }

    public void loadMoreJobByLocation(long timestamp, final String location) {
        isLoadMoreJobByLocation = false;
        mJobReference.orderBy("timestamp", Direction.DESCENDING).startAt(timestamp).limit(jobQuantity == 0 ? 1 : jobQuantity).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mMoreJobListByLocation.clear();
                            for (DocumentSnapshot d : task.getResult()) {
                                Job job = d.toObject(Job.class);
                                if (job.getStatus().equals("Đang tuyển") && job.getLocation().equals(location)) {
                                    mMoreJobListByLocation.add(job);
                                }
                                if (mMoreJobListByLocation.size() == NUMBER_DATA) {
                                    break;
                                }
                            }
                            if (mMoreJobListByLocation.size() > 0) {
                                mMoreJobListByLocation.remove(0);
                            }
                            isLoadMoreJobByLocation = true;
                        }
                    }
                });
    }

    public void removeListener(){
        if (listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    public Job getJobById() {
        return jobById;
    }

    public ArrayList<Job> getMoreJobListByLocation() {
        return mMoreJobListByLocation;
    }

    public ArrayList<Job> getJobListByLocation() {
        return mJobListByLocation;
    }

    public ArrayList<Candidate> getCandidateList() {
        return this.mCandidateList;
    }

    public ArrayList<Job> getJobs() {
        return this.mJobList;
    }

    public ArrayList<Job> getLoadMoreJobs() {
        return this.mLoadMoreList;
    }

    public int getJobQuantity() {
        return this.jobQuantity;
    }

    public static JobManager getInstance() {
        if (sInstance == null) {
            sInstance = new JobManager();
        }

        return sInstance;
    }
}
