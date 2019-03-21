package com.procrastimax.birthdaybuddy.models

import com.procrastimax.birthdaybuddy.EventDataIO
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
        return "MonthDivider${EventDataIO.divider_chars_properties}${Identifier.Date}${EventDataIO.divider_chars_values}${EventDate.parseDateToString(
            this.eventDate,
            DateFormat.DEFAULT
        )}${EventDataIO.divider_chars_properties}${Identifier.MonthName}${EventDataIO.divider_chars_values}$month_name"
    }
}