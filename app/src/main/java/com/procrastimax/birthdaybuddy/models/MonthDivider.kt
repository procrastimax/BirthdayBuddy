package com.procrastimax.birthdaybuddy.models

import com.procrastimax.birthdaybuddy.handler.IOHandler
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
        return "$Name${IOHandler.characterDivider_properties}${Identifier.Date}${IOHandler.characterDivider_values}${parseDateToString(
            this.eventDate,
            DateFormat.DEFAULT,
            Locale.GERMAN
        )}${IOHandler.characterDivider_properties}${Identifier.MonthName}${IOHandler.characterDivider_values}$month_name"
    }

    companion object {
        const val Name: String = "MonthDivider"
    }
}