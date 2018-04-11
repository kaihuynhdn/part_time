package com.example.kaihuynh.part_timejob.models;

import java.io.Serializable;

/**
 * Created by Kai on 2018-02-03.
 */

public class Candidate implements Serializable{
    private User mUser;
    private String mJobExperience;
    private long mDate;
    private String status;

    public Candidate(){

    }

    public Candidate(User mUser, String mJobExperience, long mDate, String status) {
        this.mUser = mUser;
        this.mJobExperience = mJobExperience;
        this.mDate = mDate;
        this.status = status;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public String getJobExperience() {
        return mJobExperience;
    }

    public void setJobExperience(String mJobExperience) {
        this.mJobExperience = mJobExperience;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
