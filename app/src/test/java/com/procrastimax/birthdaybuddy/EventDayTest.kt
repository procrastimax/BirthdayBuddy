package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventDay
import org.junit.Assert
import org.junit.Test
import java.util.*

class EventDayTest {
    @Test
    fun dateParsingTest() {
        println(EventDay.parseStringToDate("2/13/19"))
        println(EventDay.parseDateToString(Calendar.getInstance().time))
        Assert.assertEquals("2/13/19", EventDay.parseDateToString(EventDay.parseStringToDate("2/13/19")))
    }
}