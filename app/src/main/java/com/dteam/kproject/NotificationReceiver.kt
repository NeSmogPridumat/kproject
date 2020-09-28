package com.dteam.kproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    val id = "1"
    val name = "Notifications"
    override fun onReceive(context: Context?, intent: Intent?) {
        println("ALARM SRABOTAL!!!")

        when (intent?.action) {
            Intent.ACTION_DATE_CHANGED -> showNotification(context)
            Intent.ACTION_BOOT_COMPLETED -> showNotification(context)
        }
        showNotification(context)
    }

    private fun showNotification(context: Context?) {

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.apply {
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(context, id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.resources.getString(R.string.massage_chair))
            .setContentText(context.resources.getString(R.string.your_turn_soon))
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()
        notificationManager.notify(1, notification)
    }
}