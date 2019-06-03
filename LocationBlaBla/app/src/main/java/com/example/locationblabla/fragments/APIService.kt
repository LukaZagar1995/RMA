package com.example.locationblabla.fragments

import com.example.locationblabla.notifications.Response
import com.example.locationblabla.notifications.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface APIService {
    @Headers("Content-Type:application/json",
        "Authorization:key=AAAAWC8SSHg:APA91bFH6-W1cvQXTm6BiK8d4-DwdcuwCprz24dg-xmSfdfXCqeVKxyZ8eOyBn_c0aCTkrFW-ArygaCYCLjbPMd4n4m4XUHUAHzgoRJfc6jel8x99gegqgyf_1qFj1NB2IE7j_3ON02S")
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<Response>
}