package com.dteam.kproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    companion object{
//        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
    }

    val id = "1"
    val name = "Notifications"
    override fun onReceive(context: Context?, intent: Intent?) {
        println("RECEIVER " + context.toString())

        when (intent?.action) {
            Intent.ACTION_DATE_CHANGED -> startService(context)
            Intent.ACTION_BOOT_COMPLETED -> startService(context)
            ACTION_STOP_FOREGROUND_SERVICE -> {
                context?.stopService(Intent(context, MyService::class.java).setAction(
                    ACTION_STOP_FOREGROUND_SERVICE))
            }
            else -> startService(context)
        }
    }

    private fun startService(context: Context?){
        println("START SERVICE IN RECEIVER")
        val intentService = Intent(context, MyService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context?.startForegroundService(intentService)
        } else context?.startService(intentService)
    }
}