package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.handler.NotificationHandler
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Test
import java.text.DateFormat
import java.util.*

class NotificationTests {

    @Test
    fun calcNotificationTimeTest() {
        val cal = Calendar.getInstance()
        cal.time = EventDate.parseStringToDate("06.02.00", DateFormat.DEFAULT, Locale.GERMAN)
        val event = EventDate(cal.time)

        val noteTime_1 = NotificationHandler.getNotifcationTime(event, NotificationHandler.ReminderStart.MONTH)
        println(noteTime_1)
        println()

        val noteTime_2 = NotificationHandler.getNotifcationTime(event, NotificationHandler.ReminderStart.WEEK)
        println(noteTime_2)
        println()

        val noteTime_3 = NotificationHandler.getNotifcationTime(event, NotificationHandler.ReminderStart.DAY)
        println(noteTime_3)
        println()

        val noteTime_4 = NotificationHandler.getNotifcationTime(event, NotificationHandler.ReminderStart.EVENTDATE)
        println(noteTime_4)
        println()

        assert(true)
    }
}