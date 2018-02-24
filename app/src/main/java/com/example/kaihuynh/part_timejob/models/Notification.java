package com.example.kaihuynh.part_timejob.models;

import java.util.Date;

/**
 * Created by Kai on 2018-02-03.
 */

public class Notification {
    private String types;
    private String status;
    private Date date;
    private String content;

    public Notification(){

    }

    public Notification(String types, String status, Date date, String content) {
        this.types = types;
        this.status = status;
        this.date = date;
        this.content = content;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
