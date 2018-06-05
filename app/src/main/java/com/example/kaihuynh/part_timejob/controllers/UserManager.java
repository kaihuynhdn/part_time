package com.example.kaihuynh.part_timejob.controllers;

import android.support.annotation.NonNull;

import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Kai on 2018-02-03.
 */

public class UserManager {
    private CollectionReference mUserReference;
    private static UserManager sInstance = null;
    private User user, userById;

    private UserManager(){
        mUserReference = FirebaseFirestore.getInstance().collection("users");
    }

    public void load(User u){
        this.user = u;
    }

    public User getUser(){
        return this.user;
    }

    public void updateUser(User u){
        mUserReference.document(u.getId()).set(u);
        load(u);
    }

    public boolean isUpdated(){
        return user.getDayOfBirth() != null || user.getAddress() != null || user.getGender() != null
                || user.getPhoneNumber() != null || user.getEducation() != null
                || user.getForeignLanguages() != null || user.getSkills() != null;
    }

    public void loadUserByID(String id){
        userById=null;
        mUserReference.whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DocumentSnapshot d : task.getResult()) {
                        userById = d.toObject(User.class);
                    }
                }
            }
        });
    }

    public boolean isLikeJob(String id){
        if (user.getFavouriteJobList()!= null){
            for (Job job : user.getFavouriteJobList()){
                if (job.getId().equals(id)){
                    return true;
                }
            }
        }
        return false;
    }

    public void removeFavouriteJob(String id){
        ArrayList<Job> jobs = user.getFavouriteJobList();
        for (Job job : jobs){
            if (job.getId().equals(id)){
                jobs.remove(job);
                break;
            }
        }
        User u = user;
        u.setFavouriteJobList(jobs);
        updateUser(u);
    }

    public void updateSpecificUser(User u){
        mUserReference.document(u.getId()).set(u);
    }

    public User getUserById() {
        return userById;
    }

    public static UserManager getInstance(){
        if(sInstance == null){
            sInstance = new UserManager();
        }

        return sInstance;
    }
}
