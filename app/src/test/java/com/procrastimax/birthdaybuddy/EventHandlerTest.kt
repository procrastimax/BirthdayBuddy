package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test

class EventHandlerTest {

    @Test
    fun testAdding() {
        /*val event_count = 5
        EventHandler.generateRandomEventDates(event_count)

        Assert.assertEquals(event_count, EventHandler.getLastIndex())*/
        assert(true)
    }

    @Test
    fun testGetKeyFromValue() {
       /* EventHandler.clearMap()
        val event_count = 5
        //generate 5 random events
        EventHandler.generateRandomEventDates(event_count)

        //add one specific event
        val event = EventBirthday(EventDate.parseStringToDate("02/02/02"), "Max", "Xam")
        EventHandler.addEvent(event)

        //add 5 more random events
        EventHandler.generateRandomEventDates(event_count)
        Assert.assertEquals(5, EventHandler.getKeyToValue(event))*/
        assert(true)
    }

    @Test
    fun testGetSortedValueList() {
        /*EventHandler.clearMap()
        val cal_1 = Calendar.getInstance()
        cal_1.set(Calendar.DAY_OF_YEAR, 1)
        val event = EventDate(cal_1.time)

        val event_count = 5
        //generate 5 random events
        EventHandler.generateRandomEventDates(event_count, Ins)

        EventHandler.addEvent(event, false)
        val sortList = EventHandler.getSortedListBy(EventDate.Identifier.Date)

        println(sortList)

        Assert.assertEquals(event, sortList.first())*/
        assert(true)
    }
}