package com.procrastimax.birthdaybuddy.models

import java.text.DateFormat
import java.util.*

class MonthDivider(date: Date, val month_name: String) : EventDay(date) {

    override fun toString(): String {
        return "MonthDivider|${EventDay.parseDateToString(
            this.eventDate,
            DateFormat.SHORT
        )}|$month_name"
    }
}