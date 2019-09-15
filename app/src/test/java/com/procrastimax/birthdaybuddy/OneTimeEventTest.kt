package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.OneTimeEvent
import org.junit.Assert
import org.junit.Test
import java.util.*

class OneTimeEventTest {

    @Test
    fun daysUntilTest() {
        val current_cal = Calendar.getInstance()

        var oneTimeEvent = OneTimeEvent(current_cal.time, "test")

        Assert.assertEquals(1, oneTimeEvent.getDaysUntil())

        current_cal.set(Calendar.YEAR, current_cal.get(Calendar.YEAR) + 1)

        oneTimeEvent = OneTimeEvent(current_cal.time, "test")

        Assert.assertEquals(366, oneTimeEvent.getDaysUntil())
    }

    @Test
    fun yearsUntil() {
        val current_cal = Calendar.getInstance()

        current_cal.set(Calendar.YEAR, current_cal.get(Calendar.YEAR) + 3)

        val oneTimeEvent = OneTimeEvent(current_cal.time, "test")

        Assert.assertEquals(3, oneTimeEvent.getYearsUntil())
    }

}