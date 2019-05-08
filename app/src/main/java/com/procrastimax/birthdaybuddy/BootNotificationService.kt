package com.procrastimax.birthdaybuddy

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.handler.NotificationHandler

class BootNotificationService : JobIntentService() {
    private val JOB_ID = 602

    fun addWork(context: Context, work: Intent) {
        enqueueWork(context, BootNotificationService::class.java, JOB_ID, work)
        Log.i("BootNotificationService", "add Work called")
    }

    override fun onHandleWork(intent: Intent) {
        IOHandler.registerIO(this)
        IOHandler.readAll(this)
        NotificationHandler.scheduleListEventNotifications(this, EventHandler.getList())
        Log.i("BootNotificationService", "notifications added")
    }
}