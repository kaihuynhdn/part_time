package com.example.kaihuynh.part_timejob.controllers;

import android.support.annotation.NonNull;

import com.example.kaihuynh.part_timejob.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void updateJobStatus(User u){
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
