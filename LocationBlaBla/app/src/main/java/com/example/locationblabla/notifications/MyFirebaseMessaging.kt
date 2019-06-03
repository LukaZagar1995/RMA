package com.example.locationblabla.notifications

import android.app.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import android.os.Bundle
import com.example.locationblabla.activity.ChatActivity
import java.util.Collections.replaceAll
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.example.locationblabla.Constants
import com.google.firebase.database.FirebaseDatabase


class MyFirebaseMessaging : FirebaseMessagingService() {

    companion object {
        const val USER_ID = "userid"
    }


    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val sent = remoteMessage?.data?.get("sent")
        val user = remoteMessage?.data?.get("user")

        val preference = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentUser = preference.getString("currentuser", "none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != user) {
            if (firebaseUser != null && sent == firebaseUser.uid) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage)
                } else {
                    sendNotification(remoteMessage)
                }
            }
        }
    }

    private fun sendOreoNotification(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification = remoteMessage.notification
        val j = Integer.parseInt(user!!.replace("[\\D]", ""))
        val intent = Intent(this, ChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString(USER_ID, user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OreoNotification(this)
        val builder: Notification.Builder = oreoNotification.getOreoNotification(
            title!!,
            body!!, pendingIntent, defaultSound, icon!!
        )

        var i = 0
        if (j > 0) {
            i = j
        }
        oreoNotification.manager!!.notify(i, builder.build());
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification = remoteMessage.notification
        val j = Integer.parseInt(user!!.replace("[\\D]", ""))
        val intent = Intent(this, ChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString(USER_ID, user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)
        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if (j > 0) {
            i = j
        }
        noti.notify(i, builder.build())

    }

    private fun updateToken(newToken: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().getReference(Constants.DB_TOKEN)
        val token = newToken?.let { Token(it) }
        if (firebaseUser != null) {
            db.child(firebaseUser.uid).setValue(token)
        }
    }

}