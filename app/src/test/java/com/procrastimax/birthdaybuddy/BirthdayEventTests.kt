package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.EventBirthday
import org.junit.Assert
import org.junit.Test
import java.util.*

class BirthdayEventTests {

    @Test
    fun AgeCalcualtionTest() {
        // Same Day 5 years after birth -> 5 year old
        val birthday = Calendar.getInstance()
        birthday.set(Calendar.YEAR, birthday.get(Calendar.YEAR) - 5)
        val birthdayEvent = EventBirthday(birthday.time, "testName", true)

        Assert.assertEquals(5, birthdayEvent.getTurningAgeValue())

        // Birthday would be tomorrow, so still 4 years old
        val birthday2 = Calendar.getInstance()
        birthday2.set(Calendar.YEAR, birthday2.get(Calendar.YEAR) - 5)
        birthday2.set(Calendar.DAY_OF_YEAR, birthday2.get(Calendar.DAY_OF_YEAR) + 1)
        val birthdayEvent2 = EventBirthday(birthday2.time, "testName", true)

        Assert.assertEquals(5, birthdayEvent2.getTurningAgeValue())

        // Birthday was yesterday, so person is 5 years old
        val birthday3 = Calendar.getInstance()
        birthday3.set(Calendar.YEAR, birthday3.get(Calendar.YEAR) - 5)
        birthday3.set(Calendar.DAY_OF_YEAR, birthday3.get(Calendar.DAY_OF_YEAR) - 1)
        val birthdayEvent3 = EventBirthday(birthday3.time, "testName", true)

        Assert.assertEquals(6, birthdayEvent3.getTurningAgeValue())
    }

    @Test
    fun TurningAgeTest() {
        // Birthday exactly one year ago, so person is today turning 1
        val birthday = Calendar.getInstance()
        birthday.set(Calendar.YEAR, birthday.get(Calendar.YEAR) - 1)
        val birthdayEvent = EventBirthday(birthday.time, "testName", true)

        Assert.assertEquals(1, birthdayEvent.getTurningAgeValue())

        // Birthday would be tomorrow, so person is turning 1
        val birthday2 = Calendar.getInstance()
        birthday2.set(Calendar.YEAR, birthday2.get(Calendar.YEAR) - 1)
        birthday2.set(Calendar.DAY_OF_YEAR, birthday2.get(Calendar.DAY_OF_YEAR) + 1)
        val birthdayEvent2 = EventBirthday(birthday2.time, "testName", true)

        Assert.assertEquals(1, birthdayEvent2.getTurningAgeValue())

        // Birthday was yesterday a year ago, so person is turning 2
        val birthday3 = Calendar.getInstance()
        birthday3.set(Calendar.YEAR, birthday3.get(Calendar.YEAR) - 1)
        birthday3.set(Calendar.DAY_OF_YEAR, birthday3.get(Calendar.DAY_OF_YEAR) - 1)
        val birthdayEvent3 = EventBirthday(birthday3.time, "testName", true)

        Assert.assertEquals(2, birthdayEvent3.getTurningAgeValue())
    }

}