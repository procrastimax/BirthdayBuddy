package com.procrastimax.birthdaybuddy.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.procrastimax.birthdaybuddy.AlarmReceiver
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.models.*
import java.util.*

object NotificationHandler {

    const val CHANNEL_ID = "channel-birthdaybuddy"

    private const val NOTIFICATION_WINDOW_LENGTH: Long = 10000

    //prime factors bc. of math and so
    enum class ReminderStart(val value: Int) {
        EVENTDATE(1),
        DAY(2),
        WEEK(3),
        MONTH(4)
    }

    fun scheduleNotification(context: Context, event: EventDate) {
        if (event !is MonthDivider) {
            when (event) {
                is EventBirthday -> {
                    //do nothing when notifications for this are disabled
                    val isBirthdayReminded =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnBirthday)
                    if (isBirthdayReminded == false) return

                    //get reminding times
                    val isMonthReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeBirthday)
                    val isWeekReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeBirthday)
                    val isDayReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeBirthday)
                    val isEventDayReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayBirthday)

                    //set reminder for reminding times
                    if (isMonthReminder == true) {
                        setUpNotification(context, event, ReminderStart.MONTH)
                    }

                    if (isWeekReminder == true) {
                        setUpNotification(context, event, ReminderStart.WEEK)
                    }

                    if (isDayReminder == true) {
                        setUpNotification(context, event, ReminderStart.DAY)
                    }

                    if (isEventDayReminder == true) {
                        setUpNotification(
                            context,
                            event,
                            ReminderStart.EVENTDATE
                        )
                    }
                }
                is AnnualEvent -> {
                    //do nothing when notifications for this are disabled
                    val isAnnualReminded =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnAnnual)
                    if (isAnnualReminded == false) return

                    val isMonthReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeAnnual)
                    val isWeekReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeAnnual)
                    val isDayReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeAnnual)
                    val isEventDayReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayAnnual)

                    //set reminder for reminding times
                    if (isMonthReminder == true) {
                        setUpNotification(context, event, ReminderStart.MONTH)
                    }

                    if (isWeekReminder == true) {
                        setUpNotification(context, event, ReminderStart.WEEK)
                    }

                    if (isDayReminder == true) {
                        setUpNotification(context, event, ReminderStart.DAY)
                    }

                    if (isEventDayReminder == true) {
                        setUpNotification(
                            context,
                            event,
                            ReminderStart.EVENTDATE
                        )
                    }
                }
                is OneTimeEvent -> {
                    //do nothing when notifications for this are disabled
                    val isOneTimeReminded =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnOneTime)
                    if (isOneTimeReminded == false) return

                    //get reminding times
                    val isMonthReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeOneTime)
                    val isWeekReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeOneTime)
                    val isDayReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeOneTime)
                    val isEventDayReminder =
                        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayOneTime)

                    //set reminder for reminding times
                    if (isMonthReminder == true) {
                        setUpNotification(context, event, ReminderStart.MONTH)
                    }

                    if (isWeekReminder == true) {
                        setUpNotification(context, event, ReminderStart.WEEK)
                    }

                    if (isDayReminder == true) {
                        setUpNotification(context, event, ReminderStart.DAY)
                    }

                    if (isEventDayReminder == true) {
                        setUpNotification(
                            context,
                            event,
                            ReminderStart.EVENTDATE
                        )
                    }
                }
            }
        }
    }

    private fun getRequestCode(event: EventDate, reminderStart: ReminderStart): Int {
        return (event.toString() + reminderStart.value.toString()).hashCode()
    }

    private fun setUpNotification(
        context: Context,
        event: EventDate,
        reminderStart: ReminderStart
    ) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_EVENTSTRING, event.toString())
        intent.putExtra(
            MainActivity.FRAGMENT_EXTRA_TITLE_NOTIFICATIONID,
            getRequestCode(event, reminderStart)
        )
        intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID, event.eventID)
        val alarmIntent =
            PendingIntent.getBroadcast(context, getRequestCode(event, reminderStart), intent, 0)

        val notificationTime = getNotificationTime(event, reminderStart)
        when (event) {
            is EventBirthday -> {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    NOTIFICATION_WINDOW_LENGTH,
                    alarmIntent
                )
            }
            is AnnualEvent -> {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.time,
                    NOTIFICATION_WINDOW_LENGTH,
                    alarmIntent
                )
            }
            is OneTimeEvent -> {

                val dayAfterEvent = Calendar.getInstance().apply {
                    time = event.eventDate
                    this.set(Calendar.DAY_OF_YEAR, this.get(Calendar.DAY_OF_YEAR) + 1)
                }

                if (notificationTime.before(dayAfterEvent.time)) {
                    alarmManager.setWindow(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime.time,
                        NOTIFICATION_WINDOW_LENGTH,
                        alarmIntent
                    )
                }
            }
        }
    }

    fun getNotificationTime(event: EventDate, reminderStart: ReminderStart): Date {

        var notificationTime: String? = null

        //get special notification time for specific events
        when (event) {
            is EventBirthday -> {
                notificationTime =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeBirthday)
            }
            is AnnualEvent -> {
                notificationTime =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeAnnual)
            }
            is OneTimeEvent -> {
                notificationTime =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeOneTime)
            }
        }

        if (notificationTime == null) {
            notificationTime = "12:00"
        }

        val hour = EventDate.getHourFromTimeString(notificationTime)
        val minute = EventDate.getMinuteFromTimeString(notificationTime)

        val cal = Calendar.getInstance()
        cal.time = EventDate.dateToCurrentTimeContext(event.eventDate)

        when (reminderStart) {
            //set notification time a month before event day
            ReminderStart.MONTH -> {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1)
            }

            //set notification time a week before event day
            ReminderStart.WEEK -> {
                cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) - 1)
            }

            //set notification time a day before event day
            ReminderStart.DAY -> {
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1)
            }

            //set notification time on event day
            ReminderStart.EVENTDATE -> {

            }
        }

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)

        if (cal.time.before(Calendar.getInstance().time)) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1)
        }

        return cal.time
    }

    fun cancelNotification(context: Context, event: EventDate) {
        //check for type and check pending intents, then delete if existent
        for (it in ReminderStart.values()) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    getRequestCode(event, it),
                    intent,
                    PendingIntent.FLAG_NO_CREATE
                )

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
}