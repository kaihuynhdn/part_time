package com.example.kaihuynh.part_timejob.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kai on 2018-02-03.
 */

public class Candidate implements Serializable{
    private User mUser;
    private String mJobExperience;
    private Date mDate;
    private String status;

    public Candidate(){

    }

    public Candidate(User mUser, String mJobExperience, Date mDate, String status) {
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
