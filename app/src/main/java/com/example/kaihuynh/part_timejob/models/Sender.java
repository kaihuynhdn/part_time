package com.example.kaihuynh.part_timejob.models;

public class Sender {
    private NotificationFCM notification;
    private String to;

    public Sender(){

    }

    public Sender(String to, NotificationFCM notification) {
        this.notification = notification;
        this.to = to;
    }

    public NotificationFCM getNotification() {
        return notification;
    }
}
