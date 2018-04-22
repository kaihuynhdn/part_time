package com.example.kaihuynh.part_timejob.others;

import com.example.kaihuynh.part_timejob.remote.APIService;
import com.example.kaihuynh.part_timejob.remote.RetrofitClient;

public class Common {
    public static String currentToken = "";
    private static String baseUrl = "https://fcm.googleapis.com/";
    public static APIService getClientFCM(){
        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }
}
