package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventDay
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class EventDayTest {
    @Test
    fun dateParsingTest() {
        Assert.assertEquals("2/13/19", EventDay.parseDateToString(EventDay.parseStringToDate("2/13/19")))
    }

    @Test
    fun isInFutureTest() {
        val currentDate = Calendar.getInstance().time
        //86400000 is one day in ms
        currentDate.time += 86400001

        //primary constructor test
        val event = EventDay(currentDate)

        //setter test
        event.eventDate = currentDate

        assert(!EventDay.isDateInFuture(event.eventDate))
    }

    @Test
    fun dateDifferenceInDaysTest() {
        val calender = Calendar.getInstance()
        calender.time = Calendar.getInstance().time
        calender.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)
        calender.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1)

        val event = EventDay(calender.time)
        Assert.assertEquals(1, event.getDaysUntil())

        calender.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)
        event.eventDate = calender.time
        Assert.assertEquals(364, event.getDaysUntil())
    }
}