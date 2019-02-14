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
        if (EventDay.isDateInFuture(eventDate)) {
            Log.e(
                EventDay::class.java.simpleName,
                "Member variable EVENTDAY was in the future, it is now set to current date"
            )
            eventDate = Calendar.getInstance().time
        }
    }

    companion object {
        /**
         * parseLocalizedDateToString parses the member variable EVENTDATE to a localized string in short format
         * @param date : Date
         * @param locale : Locale = Locale.getDefault()
         * @param format : Int = DateFormat.Short
         * @return String
         */
        @JvmStatic
        fun parseDateToString(
            date: Date,
            locale: Locale = Locale.getDefault(),
            format: Int = DateFormat.SHORT
        ): String {
            return DateFormat.getDateInstance(format, locale).format(date)
        }

        /**
         * parseStringToDate parses a string in localized short format to a date which has 00:00:00 as time
         * @param date_string : String
         * @param locale : Locale = Locale.getDefault()
         * @param format : Int = DateFormat.Short
         * @return Date
         */
        @JvmStatic
        fun parseStringToDate(
            date_string: String,
            locale: Locale = Locale.getDefault(),
            format: Int = DateFormat.SHORT
        ): Date {
            val df = DateFormat.getDateInstance(format, locale)
            return df.parse(date_string)
        }


        /**
         * isDateInFuture checks if the member var eventDate is in the future (>currentDate)
         * @return Boolean
         * @param Date
         */
        @JvmStatic
        fun isDateInFuture(date: Date): Boolean {
            return date.after(Calendar.getInstance().time)
        }
    }
}