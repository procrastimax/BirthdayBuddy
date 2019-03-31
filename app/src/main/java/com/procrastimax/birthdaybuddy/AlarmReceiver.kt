package com.procrastimax.birthdaybuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "birthday toast made", Toast.LENGTH_LONG).show()
    }

    companion object {
        val REQUEST_CODE: Int = 1234
        val ACTION: String = "com.procrastimax.birthdaybuddy.NOTIFICATION"
    }
}