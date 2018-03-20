package com.example.kaihuynh.part_timejob.controllers;

import com.example.kaihuynh.part_timejob.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kai on 2018-02-03.
 */

public class UserManger {
    private DatabaseReference mUserRef;
    private static UserManger sInstance = null;
    private User user;

    private UserManger(){
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void load(User u){
        this.user = u;
    }

    public User getUser(){
        return this.user;
    }

    public void updateUser(User u){
        mUserRef.child(u.getId()).setValue(u);
    }

    public boolean isUpdated(){
        return user.getDayOfBirth() != null || user.getAddress() != null || user.getGender() != null
                || user.getPhoneNumber() != null || user.getEducation() != null
                || user.getForeignLanguages() != null || user.getSkills() != null;
    }

    public static UserManger getInstance(){
        if(sInstance == null){
            sInstance = new UserManger();
        }

        return sInstance;
    }
}
