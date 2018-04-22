package com.example.kaihuynh.part_timejob.remote;

import com.example.kaihuynh.part_timejob.models.MyResponse;
import com.example.kaihuynh.part_timejob.models.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAzCZmvWw:APA91bEIvBYk8j5XeF-s2PsqTr3RPL0ARSbIchESc3kLDo6xJZR0r-rTUorPHqa0Od5pqyXnf-N0P8kP6zoefdSdWOq3Nt2rUgLl9I8op67qfv1fSeKwEqDOeK_nz4rd09y750jwEfE5"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
