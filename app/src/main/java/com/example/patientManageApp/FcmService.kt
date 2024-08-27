package com.example.patientManageApp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.patientManageApp.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FCM", "onNewToken: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM", "onNewToken: $message")
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            sendNotification(message)
        }
    }

    private fun sendNotification(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val requestCode = (System.currentTimeMillis() / 10).toInt()
        val pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = getString(R.string.alarm_ChannelId)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.data["title"].toString())
            .setContentText(message.data["body"].toString())
            .setSmallIcon(R.drawable.warning)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(requestCode, notificationBuilder.build())
    }
}