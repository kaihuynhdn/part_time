package com.example.kaihuynh.part_timejob.controllers;

import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.Notification;
import com.example.kaihuynh.part_timejob.models.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kai on 2018-02-03.
 */

public class UserManger {
    private static UserManger sInstance = null;
    private ArrayList<User> mUserList;

    private UserManger(){
        this.mUserList = new ArrayList<>();
    }

    public void load(){

        for (int i = 0; i < 10; i++){
            User user = new User();
            user.setId(i);
            user.setFullName("");
            user.setGender("");
            user.setAddress("");
            user.setEducation("");
            user.setDayOfBirth(new Date());
            user.setForeignLanguages("");
            user.setPhoneNumber("");
            user.setSkills("");
            user.setEmail("");
            user.setPassword("");

            ArrayList<Notification> mNotificationList = new ArrayList<>();
            mNotificationList.add(new Notification("", "", new Date(), ""));
            mNotificationList.add(new Notification("", "", new Date(), ""));
            mNotificationList.add(new Notification("", "", new Date(), ""));
            user.setNotificationList(mNotificationList);

            ArrayList<Job> mFavouriteJobs = new ArrayList<>();

            for(int j = 0; j<5; j++){
                Job job = new Job();
                job.setName("");
                job.setSalary("");
                job.setBenefits("");
                job.setPostedDate(new Date());
                job.setDescription("");
                job.setLocation("");
                job.setRequirement("");

                user = new User();
                user.setFullName("");
                user.setAddress("");
                user.setDayOfBirth(new Date());
                user.setPhoneNumber("");
                user.setEmail("");

                job.setRecruiter(user);

                mFavouriteJobs.add(job);
            }

            user.setFavouriteJobList(mFavouriteJobs);

            ArrayList<Job> mAppliedJobs = new ArrayList<>();

            for(int j = 0; j<5; j++){
                Job job = new Job();
                job.setName("");
                job.setSalary("");
                job.setPostedDate(new Date());
                job.setBenefits("");
                job.setDescription("");
                job.setLocation("");
                job.setRequirement("");

                user = new User();
                user.setFullName("");
                user.setAddress("");
                user.setDayOfBirth(new Date());
                user.setPhoneNumber("");
                user.setEmail("");

                job.setRecruiter(user);

                mAppliedJobs.add(job);
            }

            user.setAppliedJobList(mAppliedJobs);

            mUserList.add(user);
        }

    }

    public ArrayList<User> getUsers(){

        return this.mUserList;
    }

    public User getUserByEmail(String email){
        for(User u : mUserList){
            if(u.getEmail().equalsIgnoreCase(email)){
                return u;
            }
        }
        return null;
    }

    public ArrayList<Job> getFavouriteJobListById(int id){
        for(User u : mUserList){
            if(u.getId() == id){
                return u.getFavouriteJobList();
            }
        }

        return null;
    }

    public ArrayList<Job> getAppliedJobListById(int id){
        for(User u : mUserList){
            if(u.getId() == id){
                return u.getAppliedJobList();
            }
        }

        return null;
    }

    public User getUserById(int id){
        for(User u : mUserList){
            if(u.getId() == id){
                return u;
            }
        }
        return null;
    }

    public ArrayList<Notification> getUserNotificationById(int id){
        for(User u : mUserList){
            if(u.getId() == id){
                return u.getNotificationList();
            }
        }
        return null;
    }

    public static UserManger getInstance(){
        if(sInstance == null){
            sInstance = new UserManger();
        }

        return sInstance;
    }


}
