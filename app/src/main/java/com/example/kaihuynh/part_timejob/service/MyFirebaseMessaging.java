package com.example.kaihuynh.part_timejob.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.kaihuynh.part_timejob.HomePageActivity;
import com.example.kaihuynh.part_timejob.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification());
    }

    private void showNotification(RemoteMessage.Notification notification) {
        int id = (int) System.currentTimeMillis();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()));

        Intent intent =  new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (HomePageActivity.isDestroyed){
            PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, builder.build());
        }else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, builder.build());
            HomePageActivity.getInstance().getViewPager().setCurrentItem(3);
        }



    }
}
