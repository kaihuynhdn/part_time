package com.example.kaihuynh.part_timejob.service;

import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.Common;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Common.currentToken = refreshedToken;
        User u = UserManager.getInstance().getUser();
        if (u!=null && refreshedToken!=null){
            u.setToken(refreshedToken);
            UserManager.getInstance().updateUser(u);
        }
    }
}
