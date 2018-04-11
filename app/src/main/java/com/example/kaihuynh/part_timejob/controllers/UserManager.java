package com.example.kaihuynh.part_timejob.controllers;

import com.example.kaihuynh.part_timejob.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Kai on 2018-02-03.
 */

public class UserManager {
    private CollectionReference mUserReference;
    private static UserManager sInstance = null;
    private User user;

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

    public static UserManager getInstance(){
        if(sInstance == null){
            sInstance = new UserManager();
        }

        return sInstance;
    }
}
