package com.example.kaihuynh.part_timejob.models;

public class Sender {
    private NotificationFCM notification;
    private String to;

    public Sender(){

    }

    public Sender(NotificationFCM notification, String to) {
        this.notification = notification;
        this.to = to;
    }

    public NotificationFCM getNotification() {
        return notification;
    }
}
