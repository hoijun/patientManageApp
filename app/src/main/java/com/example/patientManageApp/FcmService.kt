package com.example.patientManageApp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.getResult
import com.example.patientManageApp.domain.utils.onSuccess
import com.example.patientManageApp.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var useCases: UseCases

    override fun onNewToken(token: String) {
        Log.d("FCM", "onNewToken: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM", "remoteMessage: $message")
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                useCases.getOccurrenceJPG(message.data["date"].toString()).getResult(
                    success = {
                        withContext(Dispatchers.Main) {
                            sendNotification(message, uriToBitmap(it.data))
                        }
                    },
                    error = {
                        withContext(Dispatchers.Main) {
                            sendNotification(message, null)
                        }
                    }
                )
            }
        }
    }

    private suspend fun uriToBitmap(uri: Uri): Bitmap? {
        val loader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(uri)
            .allowHardware(false)
            .build()

        return when (val result = loader.execute(request)) {
            is SuccessResult -> (result.drawable as? BitmapDrawable)?.bitmap
            is ErrorResult -> {
                null
            }
        }
    }

    private fun sendNotification(message: RemoteMessage, jpgBitmap: Bitmap?) {
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        if (jpgBitmap != null) {
            val bigStyle = NotificationCompat.BigPictureStyle().also {
                it.bigPicture(jpgBitmap)
            }

            notificationBuilder.setStyle(bigStyle)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(requestCode, notificationBuilder.build())
    }
}