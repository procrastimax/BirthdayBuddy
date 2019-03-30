package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class EventBirthdayTest {

    @Test
    fun birthdayToStringTest() {
        val birthday = EventBirthday(
            EventDate.parseStringToDate("06.02.00", DateFormat.DEFAULT, Locale.GERMAN),
            "Max",
            "Mustermann"
        )

        birthday.nickname = "procrastimax"
        Assert.assertEquals(
            "Birthday||Forename::Max||Surname::Mustermann||Date::06.02.0001||IsYearGiven::true||Nickname::procrastimax",
            birthday.toString()
        )

        val birthday2 = EventBirthday(
            EventDate.parseStringToDate("06.02.00", DateFormat.DEFAULT, Locale.GERMAN),
            "Max",
            "Mustermann"
        )

        birthday2.nickname = "procrastimax"
        birthday2.note = "nothing"
        Assert.assertEquals(
            "Birthday||Forename::Max||Surname::Mustermann||Date::06.02.0001||IsYearGiven::true||Note::nothing||Nickname::procrastimax",
            birthday2.toString()
        )

    }

    @Test
    fun memberSettingTest() {
        val birthday_1 = EventBirthday(Calendar.getInstance().time, "Max", "Mustermann")
        birthday_1.forename = "SchMax"
        birthday_1.surname = "Niceroth"
        Assert.assertEquals("SchMax", birthday_1.forename)
        Assert.assertEquals("Niceroth", birthday_1.surname)

        birthday_1.forename = "  "
        birthday_1.surname = ""
        Assert.assertEquals("-", birthday_1.forename)
        Assert.assertEquals("0", birthday_1.surname)

        //no note was set, default is empty
        Assert.assertEquals(null, birthday_1.note)

        birthday_1.note = "Möchte ein Fahrrad haben."
        Assert.assertEquals("Möchte ein Fahrrad haben.", birthday_1.note)
    }

    @Test
    fun returnVeryShortPrettyDateTest() {
        val birthday = EventBirthday(
            EventDate.parseStringToDate("06.02.00", DateFormat.SHORT, Locale.GERMAN),
            "Max",
            "Mustermann"
        )
        Assert.assertEquals("06.02", birthday.getPrettyShortStringWithoutYear(Locale.GERMAN))
    }
}