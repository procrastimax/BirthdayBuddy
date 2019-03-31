package com.procrastimax.birthdaybuddy.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.procrastimax.birthdaybuddy.AlarmReceiver
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.OneTimeEvent
import java.text.DateFormat
import java.util.*

object NotificationHandler {

    fun scheduleNotification(context: Context,event : EventDate) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationTime = getNotifcationTime(event)

        when (event) {
            is EventBirthday -> {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    getMillisecondsBetweenTwoYears(notificationTime),
                    pendingIntent
                )
            }
            is AnnualEvent -> {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    getMillisecondsBetweenTwoYears(notificationTime),
                    pendingIntent
                )
            }
            is OneTimeEvent -> {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    5000,
                    pendingIntent
                )
            }
        }
    }

    private fun getNotifcationTime(event: EventDate): Date {
        var notificationTime: String? = IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTime)
        if (notificationTime == null) {
            notificationTime = "12:00"
        }

        val hour = EventDate.getHourFromTimeString(notificationTime)
        val minute = EventDate.getMinuteFromTimeString(notificationTime)

        val cal = Calendar.getInstance()
        cal.time = EventDate.dateToCurrentTimeContext(event.eventDate)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)

        println("notification time is: " + EventDate.parseDateToString(cal.time, DateFormat.FULL) + " - " + hour + ":" + minute)

        return cal.time
    }

    /**
     * getMillisecondsBetweenTwoYears increments the year of one date by one and returns the difference between the incremented date and the parameter date in ms
     */
    private fun getMillisecondsBetweenTwoYears(date: Date): Long {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1)
        return cal.timeInMillis - date.time
    }
}