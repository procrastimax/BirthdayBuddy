package com.procrastimax.birthdaybuddy.models

import java.text.DateFormat
import java.util.*

class MonthDivider(date: Date, val month_name: String) : EventDate(date) {

    /**
     * Identifier is an identifier for sorting
     */
    enum class Identifier : SortIdentifier {
        Date {
            override fun Identifier(): Int = 0
        },
        MonthName {
            override fun Identifier(): Int = 1
        }
    }

    override fun toString(): String {
        return "MonthDivider||${Identifier.Date}::${EventDate.parseDateToString(
            this.eventDate,
            DateFormat.DEFAULT
        )}||${Identifier.MonthName}::$month_name"
    }
}