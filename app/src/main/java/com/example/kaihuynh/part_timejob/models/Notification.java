package com.example.kaihuynh.part_timejob.models;

import java.io.Serializable;


public class Notification implements Serializable{
    public static final int TO_CANDIDATE = 1, TO_RECRUITER=2;
    public static final int STATUS_NOT_SEEN = 1, STATUS_SEEN=2;
    private int types;
    private int status;
    private long date;
    private String content;
    private Job job;
    private String avatarSender;

    public Notification(){

    }

    public Notification(int types, int status, long date, String content) {
        this.types = types;
        this.status = status;
        this.date = date;
        this.content = content;
    }

    public String getAvatarSender() {
        return avatarSender;
    }

    public void setAvatarSender(String avatarSender) {
        this.avatarSender = avatarSender;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
