package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class EventDateTest {

    @Test
    fun dateToString() {
        val date = EventDate(EventDate.parseStringToDate("06.02.01", DateFormat.DEFAULT, Locale.GERMAN))
        Assert.assertEquals("EventDate||Date::Feb 6, 0001", date.toString())
    }

    @Test
    fun isInFutureTest() {
        val currentDate = Calendar.getInstance().time
        //86400000 is one day in ms
        currentDate.time += 86400001

        //primary constructor test
        val event = EventDate(currentDate)

        //this almost always asserts to false, because if a future date is given, then the current is applied to the "future" date
        Assert.assertEquals(false, EventDate.isDateInFuture(event.eventDate))
    }

    @Test
    fun dateDifferenceInDaysTest() {
        val calender = Calendar.getInstance()
        calender.time = Calendar.getInstance().time
        calender.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)
        calender.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1)

        val event = EventDate(calender.time)
        Assert.assertEquals(1, event.getDaysUntil())

        calender.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)
        event.eventDate = calender.time
        Assert.assertEquals(365, event.getDaysUntil())
    }

    @Test
    fun yearDifferenceTest() {
        //date 5years and one day before current day
        val cal_1 = Calendar.getInstance()
        cal_1.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 5)
        cal_1.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)

        val event = EventDate(cal_1.time)
        Assert.assertEquals(5, event.getYearsSince())

        //date 5years in the past but day of year is ahead
        val cal_2 = Calendar.getInstance()
        cal_2.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 5)
        cal_2.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1)

        event.eventDate = cal_2.time
        Assert.assertEquals(4, event.getYearsSince())

        //current day/ day in future
        val cal_3 = Calendar.getInstance()
        cal_3.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1)

        event.eventDate = cal_3.time
        Assert.assertEquals(0, event.getYearsSince())
    }
}