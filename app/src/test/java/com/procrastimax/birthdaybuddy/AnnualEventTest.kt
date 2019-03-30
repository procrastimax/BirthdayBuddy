package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class AnnualEventTest {

    @Test
    fun birthdayToStringTest() {
        val anniversary = AnnualEvent(
            EventDate.parseStringToDate("06.02.00", DateFormat.DEFAULT, Locale.GERMAN),
            "isAName",
            true
        )

        Assert.assertEquals(
            "AnnualEvent||Name::isAName||Date::06.02.0001||HasStartYear::true",
            anniversary.toString()
        )

        val anniversary2 = AnnualEvent(
            EventDate.parseStringToDate("06.02.02", DateFormat.DEFAULT, Locale.GERMAN),
            "Maximilian",
            false
        )

        anniversary2.note = "nothing"
        Assert.assertEquals(
            "AnnualEvent||Name::Max||Date::06.02.0002||HasStartYear::false||Note::nothing",
            anniversary2.toString()
        )
    }
}