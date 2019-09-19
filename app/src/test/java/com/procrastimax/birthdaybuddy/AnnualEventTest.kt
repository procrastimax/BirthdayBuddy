package com.procrastimax.birthdaybuddy

import com.procrastimax.birthdaybuddy.models.AnnualEvent
import org.junit.Assert
import org.junit.Test
import java.util.*

class AnnualEventTest {

    @Test
    fun getXTimesSinceStartingTest() {
        // Same Day 1 years after first happening -> takes place for first time, since 1 year
        val annual = Calendar.getInstance()
        annual.set(Calendar.YEAR, annual.get(Calendar.YEAR) - 1)
        val annualEvent = AnnualEvent(annual.time, "testName", true)

        Assert.assertEquals(0, annualEvent.getYearsSince())
        Assert.assertEquals(1, annualEvent.getXTimesSinceStarting())

        // 1 day after happening for first time -> takes place for 2nd time since 1 year
        val annual1 = Calendar.getInstance()
        annual1.set(Calendar.YEAR, annual1.get(Calendar.YEAR) - 1)
        annual1.set(Calendar.DAY_OF_YEAR, annual1.get(Calendar.DAY_OF_YEAR) - 1)
        val annualEvent1 = AnnualEvent(annual1.time, "testName", true)

        Assert.assertEquals(1, annualEvent1.getYearsSince())
        Assert.assertEquals(2, annualEvent1.getXTimesSinceStarting())

        // 1 day before happening for first time -> takes place for 1st time since 0 year
        val annual2 = Calendar.getInstance()
        annual2.set(Calendar.YEAR, annual2.get(Calendar.YEAR) - 1)
        annual2.set(Calendar.DAY_OF_YEAR, annual2.get(Calendar.DAY_OF_YEAR) + 1)
        val annualEvent2 = AnnualEvent(annual2.time, "testName", true)

        Assert.assertEquals(0, annualEvent2.getYearsSince())
        Assert.assertEquals(1, annualEvent2.getXTimesSinceStarting())
    }
}