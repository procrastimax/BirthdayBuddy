package com.procrastimax.birthdaybuddy.models

import android.util.Log
import java.text.DateFormat
import java.util.*

/**
 * A model class to provide basic event data.
 *
 * This is a base class to be derived from.
 * Main functionality is to check if a date is valid.
 * Can be used for other eventType classes f.e. birthdayEvent, anniversaryEvent, ...
 * The used date format used in the app is dd.MM.yyyy
 *
 * @param eventDate The date of the event
 * @author Procrastimax
 */
class EventDay(private var eventDate: Date) {

    init {
        if (isDateInFuture()) {
            Log.e(
                EventDay::class.java.simpleName,
                "Member variable EVENTDAY was in the future, it is now set to current date"
            )
            eventDate = Calendar.getInstance().time
        }
    }

    /**
     * isDateInFuture checks if the member var eventDate is in the future (>currentDate)
     * @return Boolean
     */
    private fun isDateInFuture(): Boolean {
        return this.eventDate.after(Calendar.getInstance().time)
    }

    companion object {
        /**
         * parseDateToString parses the member variable EVENTDATE to a localized string in short format
         * @param date
         * @return String
         */
        fun parseDateToString(date: Date): String {
            return DateFormat.getDateInstance(DateFormat.SHORT).format(date)
        }

        /**
         * parseStringToDate parses a string in localized short format to a date which has 00:00:00 as time
         * @param date_string
         * @return Date
         */
        fun parseStringToDate(date_string: String): Date {
            val df = DateFormat.getDateInstance(DateFormat.SHORT)
            return df.parse(date_string)
        }
    }
}