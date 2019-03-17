package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import org.junit.Assert
import org.junit.Test
import java.text.DateFormat
import java.util.*

class EventBirthdayTest {

    @Test
    fun constructorTest() {
        val birthday_1 = EventBirthday(Calendar.getInstance().time, "Maximilian", "Mustermann")
        Assert.assertEquals("MaximilianMustermann", birthday_1.forename + birthday_1.surname)

        val birthday_2 = EventBirthday(Calendar.getInstance().time, "", "   ")
        Assert.assertEquals("--", birthday_2.forename + birthday_2.surname)
    }

    @Test
    fun memberSettingTest() {
        val birthday_1 = EventBirthday(Calendar.getInstance().time, "Maximilian", "Mustermann")
        birthday_1.forename = "Schmaximilian"
        birthday_1.surname = "Niceroth"
        Assert.assertEquals("Schmaximilian", birthday_1.forename)
        Assert.assertEquals("Niceroth", birthday_1.surname)

        birthday_1.forename = "  "
        birthday_1.surname = ""
        Assert.assertEquals("-", birthday_1.forename)
        Assert.assertEquals("0", birthday_1.surname)

        //no note was set, default is empty
        Assert.assertEquals("-", birthday_1.note)

        birthday_1.note = "Möchte ein Fahrrad haben."
        Assert.assertEquals("Möchte ein Fahrrad haben.", birthday_1.note)
    }

    @Test
    fun returnVeryShortPrettyDateTest() {
        val birthday = EventBirthday(
            EventDate.parseStringToDate("06.02.00", DateFormat.SHORT, Locale.GERMAN),
            "Maximilian",
            "Mustermann"
        )
        Assert.assertEquals("06.02", birthday.getPrettyShortStringWithoutYear(Locale.GERMAN))
    }
}