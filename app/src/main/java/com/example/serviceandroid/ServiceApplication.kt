package com.example.serviceandroid

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ServiceApplication : Application() {

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
    }

    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    @SuppressLint("WrongConstant")
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,"ServiceAndroid",importance)
            channel.setSound(null,null)
            val notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
