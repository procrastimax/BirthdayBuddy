package com.procrastimax.birthdaybuddy.handler

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class NotificationServiceHandler : Service() {

    private val timeNotificationServiceHandler = TimedEventNotificationHandler()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        timeNotificationServiceHandler.registerContext(this.applicationContext)
        timeNotificationServiceHandler.startTimer()

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(this.toString(), "Service created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        timeNotificationServiceHandler.stopTimer()
        Log.i(this.toString(), "Service created!")
    }
}