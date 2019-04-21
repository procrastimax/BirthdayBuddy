package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventDateTest {

    @Test
    fun dateToString() {
        val date = EventDate(EventDate.parseStringToDate("06.02.01", DateFormat.DEFAULT, Locale.GERMAN))
        Assert.assertEquals("EventDate||Date::06.02.0001", date.toString())
    }

    @Test
    fun dateFormatTest() {
        val cal = Calendar.getInstance()
        println(EventDate.parseDateToString(cal.time, DateFormat.SHORT))

        val dateString = EventDate.getDayMonthDateString(cal.time, Locale.JAPANESE )
        println(dateString)

        //val fmtOut = SimpleDateFormat(dateFormat.toString())
        //println(fmtOut.format(cal.time))

        assert(true)
    }

    @Test
    fun isInFutureTest() {
        val currentDate = Calendar.getInstance().time
        //86400000 is one day in ms
        currentDate.time += 86400001

        //primary constructor test
        val event = EventDate(currentDate)

        //this almost always asserts to false, because if a future date is given, then the current is applied to the "future" date
        Assert.assertEquals(true, EventDate.isDateInFuture(event.eventDate))
    }

    @Test
    fun dateDifferenceInDaysTest() {
        val calender = Calendar.getInstance()
        calender.time = Calendar.getInstance().time
        calender.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)
        calender.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1)

        val event = EventDate(calender.time)
        Assert.assertEquals(2, event.getDaysUntil())

        calender.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)
        event.eventDate = calender.time
        Assert.assertEquals(366, event.getDaysUntil())
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
    }
}