package com.example.locationblabla.notifications

import android.content.Context
import android.content.ContextWrapper
import android.app.PendingIntent
import android.os.Build
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import android.net.Uri


class OreoNotification(base: Context) : ContextWrapper(base) {

    companion object {

        private val CHANNEL_ID = "com.example.locationblabla"
        private val CHANNEL_NAME = "locationblabla"
    }

    private var notificationManager: NotificationManager? = null

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) {
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            return notificationManager
        }

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        manager!!.createNotificationChannel(channel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getOreoNotification(
        title: String, body: String,
        pendingIntent: PendingIntent, soundUri: Uri, icon: String
    ): Notification.Builder {
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(Integer.parseInt(icon))
            .setSound(soundUri)
            .setAutoCancel(true)
    }


}
