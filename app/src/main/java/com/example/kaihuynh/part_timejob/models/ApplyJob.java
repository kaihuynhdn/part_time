package com.example.kaihuynh.part_timejob.models;

import com.example.kaihuynh.part_timejob.models.Job;

import java.io.Serializable;

public class ApplyJob implements Serializable{
    public final static String VIEWING_STATUS = "Đang chờ", EMPLOYED_STATUS = "Được tuyển", UNEMPLOYED_STATUS = "Từ chối";
    private Job job;
    private String status;

    public ApplyJob(){

    }

    public ApplyJob(Job job, String status) {
        this.job = job;
        this.status = status;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
