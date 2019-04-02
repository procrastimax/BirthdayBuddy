package com.procrastimax.birthdaybuddy.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.procrastimax.birthdaybuddy.AlarmReceiver
import com.procrastimax.birthdaybuddy.models.*
import java.util.*

object NotificationHandler {

    const val CHANNEL_ID = "channel-birthdaybuddy"

    //prime factors bc. of math and so
    enum class ReminderStart(val value: Int) {
        EVENTDATE(1),
        DAY(3),
        WEEK(5),
        MONTH(7)
    }

    fun scheduleNotification(context: Context, event: EventDate) {
        if (event !is MonthDivider) {

            /*val isMonthReminder = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_Month)
            val isDayReminder = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_Day)
            val isEventDayReminder = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_EventDay)
            val isWeekReminder = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_Week)

            val isBirthdayReminded = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotification_Birthday)
            val isAnnualReminded = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotification_AnnualEvent)
            val isOneTimeReminded =
                IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotification_OneTimeEvent)

            //when event type does not match to settings, don't throw notification
            if (isBirthdayReminded == false and (event is EventBirthday)) {
                return
            }
            if (isAnnualReminded == false and (event is AnnualEvent)) {
                return
            }
            if (isOneTimeReminded == false and (event is OneTimeEvent)) {
                return
            }

            if (isMonthReminder == true) {
                setUpNotification(context, event, NotificationHandler.ReminderStart.MONTH)
            }

            if (isWeekReminder == true) {
                setUpNotification(context, event, NotificationHandler.ReminderStart.WEEK)
            }

            if (isDayReminder == true) {
                setUpNotification(context, event, NotificationHandler.ReminderStart.DAY)
            }

            if (isEventDayReminder == true) {
                setUpNotification(context, event, NotificationHandler.ReminderStart.EVENTDATE)
            }*/
        }
    }

    private fun setUpNotification(context: Context, event: EventDate, reminderStart: ReminderStart) {

        val intent = Intent(context, AlarmReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent.putExtra("EVENTSTRING", event.toString())
        intent.putExtra("NOTIFICATIONID", event.eventID * reminderStart.value)
        val alarmIntent =
            PendingIntent.getBroadcast(context, event.eventID * reminderStart.value, intent, 0)


        val notificationTime = getNotifcationTime(event, reminderStart)

        //TODO: set window values higher
        when (event) {
            is EventBirthday -> {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    1000,
                    alarmIntent
                )
                //println(" ---> EventBirthday notification added on " + notificationTime + " with ID: " + event.eventID * reminderStart.value)
            }
            is AnnualEvent -> {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    1000,
                    alarmIntent
                )
                //println(" ---> AnnualEvent notification added on " + notificationTime + " with ID: " + event.eventID * reminderStart.value)
            }
            is OneTimeEvent -> {
                val calNotif = Calendar.getInstance()
                val calEvent = Calendar.getInstance()

                calNotif.time = notificationTime
                calEvent.time = event.eventDate

                if (calNotif.get(Calendar.DAY_OF_YEAR) <= calEvent.get(Calendar.DAY_OF_YEAR)) {
                    alarmManager.setWindow(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime.time,
                        1000,
                        alarmIntent
                    )
                    //println(" ---> OneTimeEvent notification added on " + notificationTime + " with ID: " + event.eventID * reminderStart.value)
                }
            }
        }
    }

    fun getNotifcationTime(event: EventDate, reminderStart: ReminderStart): Date {
        /*var notificationTime: String? = IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTime)
        if (notificationTime == null) {
            notificationTime = "12:00"
        }

        val hour = EventDate.getHourFromTimeString(notificationTime)
        val minute = EventDate.getMinuteFromTimeString(notificationTime)

        val cal = Calendar.getInstance()
        cal.time = EventDate.dateToCurrentTimeContext(event.eventDate)
        when (reminderStart) {
            //set notification time a month before event day
            NotificationHandler.ReminderStart.MONTH -> {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1)
            }

            //set notification time a week before event day
            NotificationHandler.ReminderStart.WEEK -> {
                cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) - 1)
            }

            //set notification time a day before event day
            NotificationHandler.ReminderStart.DAY -> {
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1)
            }

            //set notification time on event day
            NotificationHandler.ReminderStart.EVENTDATE -> {

            }
        }

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)

        if (cal.time.before(Calendar.getInstance().time)) {
            cal.time = EventDate.dateToCurrentTimeContext(cal.time)
        }*/

        //return cal.time
        return Calendar.getInstance().time
    }

    fun cancelNotification(context: Context, event: EventDate) {
        //check for type and check pending intents, then delete if existent
        for (it in ReminderStart.values()) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, event.eventID * it.value, intent, PendingIntent.FLAG_NO_CREATE)

            if (pendingIntent != null) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    fun cancelAllNotifications(context: Context, events: List<EventDate>) {
        events.forEach {
            cancelNotification(context, it)
        }
    }

    fun scheduleListEventNotifications(context: Context, events: List<EventDate>) {
        events.forEach {
            if (it !is MonthDivider) {
                scheduleNotification(context, it)
            }
        }
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