package com.procrastimax.birthdaybuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val bootService = BootNotificationService()
            if (context != null) {
                bootService.addWork(context, intent)
                Log.i("BootReceiver","work added")
            }
        }
    }
}