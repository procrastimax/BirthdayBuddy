package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import com.procrastimax.birthdaybuddy.Handler.EventHandler
import org.junit.Assert
import org.junit.Test
import java.util.*

class EventHandlerTest {

    @Test
    fun testAdding() {
        val event_count = 5
        EventHandler.generateRandomEventDates(event_count)

        Assert.assertEquals(event_count, EventHandler.getLastIndex())
    }

    @Test
    fun testGetKeyFromValue() {
        EventHandler.clearMap()
        val event_count = 5
        //generate 5 random events
        EventHandler.generateRandomEventDates(event_count)

        //add one specific event
        val event = EventBirthday(EventDay.parseStringToDate("02/02/02"), "Max", "Xam")
        EventHandler.addEvent(event)

        //add 5 more random events
        EventHandler.generateRandomEventDates(event_count)
        Assert.assertEquals(5, EventHandler.getKeyToValue(event))
    }

    @Test
    fun testGetSortedValueList() {
        EventHandler.clearMap()
        val event = EventDay(Calendar.getInstance().time)
        val event_count = 5
        //generate 5 random events
        EventHandler.generateRandomEventDates(event_count)

        EventHandler.addEvent(event)
        val sortList = EventHandler.getSortedValueListBy(EventDay.Identifier.Date)
        println(sortList)

        Assert.assertEquals(event, sortList.last())

    }
}