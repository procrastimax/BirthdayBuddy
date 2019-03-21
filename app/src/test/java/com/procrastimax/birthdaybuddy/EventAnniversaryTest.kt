package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventAnniversary
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class EventAnniversaryTest {


    @Test
    fun birthdayToStringTest() {
        val anniversary = EventAnniversary(
            EventDate.parseStringToDate("06.02.00", DateFormat.DEFAULT, Locale.GERMAN),
            "isAName",
            true
        )

        Assert.assertEquals(
            "Anniversary||Name::isAName||Date::Feb 6, 0001||HasStartYear::true",
            anniversary.toString()
        )

        val anniversary2 = EventAnniversary(
            EventDate.parseStringToDate("06.02.02", DateFormat.DEFAULT, Locale.GERMAN),
            "Maximilian",
            false
        )

        anniversary2.note = "nothing"
        Assert.assertEquals(
            "Anniversary||Name::Maximilian||Date::Feb 6, 0002||HasStartYear::false||Note::nothing",
            anniversary2.toString()
        )
    }
}