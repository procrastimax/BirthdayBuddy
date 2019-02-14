package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventDay
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class EventDayTest {
    @Test
    fun dateParsingTest() {
        println(EventDay.parseStringToDate("13.2.19", Locale.GERMAN))
        println(EventDay.parseDateToString(Calendar.getInstance().time, Locale.GERMAN, DateFormat.FULL))
        Assert.assertEquals("2/13/19", EventDay.parseDateToString(EventDay.parseStringToDate("2/13/19")))
    }

    @Test
    fun isInFutureTest() {
        assert(!EventDay.isDateInFuture(EventDay.parseStringToDate("2/14/19")))
    }
}