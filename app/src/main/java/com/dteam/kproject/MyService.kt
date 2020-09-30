package com.dteam.kproject

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat

class MyService : Service() {
    val id = "1"
    val name = "Notifications"
    lateinit var ringtone: Ringtone

    override fun onCreate() {
        println("SERVICE ONCREATE")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        println("SERVICE STAT COMMAND")
        if(intent?.action == NotificationReceiver.ACTION_STOP_FOREGROUND_SERVICE){
            println("SERVICE STOP")
            stop()
        } else {
            val notification = showNotification()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            uri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(baseContext, uri)
            ringtone.play()
            startForeground(1, notification)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showNotification(): Notification {

        val notificationManager =
            baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.apply {
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return NotificationCompat.Builder(baseContext, id)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(baseContext.resources.getString(R.string.massage_chair))
            .setContentText(baseContext.resources.getString(R.string.your_turn_soon))
            .setAutoCancel(true)
//            .addAction(R.drawable.ic_iconfinder_close, "Хорошо", createBroadcastIntent())
            .setContentIntent(createBroadcastIntent())
//            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()
    }

    override fun onDestroy() {
        ringtone.stop()
        super.onDestroy()
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }

    private fun createBroadcastIntent(
    ): PendingIntent {
        val broadcastIntent = Intent(applicationContext, NotificationReceiver::class.java)
        broadcastIntent.action = NotificationReceiver.ACTION_STOP_FOREGROUND_SERVICE
        return PendingIntent.getBroadcast(baseContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
