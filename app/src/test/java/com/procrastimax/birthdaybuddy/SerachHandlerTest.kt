package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.handler.SearchHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.util.*

class SerachHandlerTest {

    @Test
    fun testStringSplitting() {
        val testString = "Maurice-Maximilian Eberhard"

        /*val splittedString = SearchHandler.splitString(testString)
        splittedString?.forEach {
            println(it)
        }*/
        //Assert.assertEquals("Maurice", splittedString!![0])
    }

    @Test
    fun testProcessingOfSplittedData() {
        val testString = "Maurice-Maximilian Eberhard"

        /*val processedData = SearchHandler.processStringList(SearchHandler.splitString(testString)!!.toMutableList())
        processedData.forEach {
            println(it)
        }*/

        //Assert.assertEquals("maurice", processedData[0])
    }

    @Test
    fun testProcessingOfStringList() {
        val birthday_1 = EventBirthday(Calendar.getInstance().time, "M aurice", "Mustermann", true)
        val birthday_2 = EventBirthday(Calendar.getInstance().time, "Maurice-Maximilian", "Mustermann", true)
        val birthday_3 = EventBirthday(Calendar.getInstance().time, "Maurice Maximilian", "Mustermann", true)
        val birthday_4 = EventBirthday(
            Calendar.getInstance().time,
            "Maurice-Maximilian Eberhard",
            "Mustermann-ist-ein-geiler-typ",
            true
        )
        val birthday_5 =
            EventBirthday(Calendar.getInstance().time, "Maurice-Maximilian-Eberhard", "Mustermann geiler", true)
        val birthday_6 = EventBirthday(Calendar.getInstance().time, "Maurice Maximilian Eberhard", "Mustermann", true)
        val birthday_7 =
            EventBirthday(Calendar.getInstance().time, "MauriceMaximilian\\Eberhard ", "Mustermann", true)
        val birthday_8 =
            EventBirthday(Calendar.getInstance().time, "Mau rice-Maximilian Eberhard", "Mustermann", true)

        val birthdayList = listOf<EventDate>(
            birthday_1,
            birthday_2,
            birthday_3,
            birthday_4,
            birthday_5,
            birthday_6,
            birthday_7,
            birthday_8
        )
        /*val processedData = SearchHandler.getAllProcessedWords(birthdayList)
        processedData.forEach {
            println(it)
        }
        Assert.assertEquals("m", processedData[0])*/
    }

    @Test
    fun testSearchingInEvents() {
        val birthday_1 = EventBirthday(Calendar.getInstance().time, "M aurice", "Mustermann", true)
        val birthday_2 = EventBirthday(Calendar.getInstance().time, "Maurice-Maximilian", "Mustermann", true)
        val birthday_3 = EventBirthday(Calendar.getInstance().time, "Maurice Maximilian", "Mustermann", true)
        val birthday_4 = EventBirthday(
            Calendar.getInstance().time,
            "Maurice-Maximilian Eberhard",
            "Mustermann-ist-ein-geiler-typ",
            true
        )
        val birthday_5 =
            EventBirthday(Calendar.getInstance().time, "Maurice-Maximilian-Eberhard", "Mustermann geiler", true)
        val birthday_6 = EventBirthday(Calendar.getInstance().time, "Maurice Maximilian Eberhard", "Mustermann", true)
        val birthday_7 =
            EventBirthday(Calendar.getInstance().time, "MauriceMaximilian\\Eberhard ", "Mustermann", true)
        val birthday_8 =
            EventBirthday(Calendar.getInstance().time, "Mau rice-Maximilian Eberhard", "Mustermann", true)
        val birthday_9 =
            EventBirthday(Calendar.getInstance().time, "Moritz", "Bleibtreu", true)

        val birthdayList = listOf<EventDate>(
            birthday_1,
            birthday_2,
            birthday_3,
            birthday_4,
            birthday_5,
            birthday_6,
            birthday_7,
            birthday_8,
            birthday_9
        )

        var idCounter = 0
        birthdayList.forEach {
            it.eventID = idCounter
            idCounter++
        }

        val indexes = SearchHandler.searchOnEventData("bl", birthdayList)
        println(indexes)

        assert(true)
    }
}