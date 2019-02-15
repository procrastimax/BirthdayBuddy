package com.procrastimax.birthdaybuddy.models

import android.util.Log
import java.text.DateFormat
import java.time.Duration
import java.util.*

/**
 * A model class to provide basic event data.
 *
 * This is a base class to be derived from.
 * Main functionality is to check if a date is valid.
 * Can be used for other eventType classes f.e. birthdayEvent, anniversaryEvent, ...
 * The used date format used in the app is dd.MM.yyyy
 *
 * @param _eventDate The date of the event
 * @author Procrastimax
 */
open class EventDay(private var _eventDate: Date) {

    //short "hack" to make it possible to set a getter/setter for primary constructed class members
    // eventDate is used as an alibi member
    var eventDate: Date
        get() = _eventDate
        set(value) {
            _eventDate = if (EventDay.isDateInFuture(value)) {
                Log.e(
                    "EventDay",
                    "Member variable EVENTDAY was in the future, it is now set to current date"
                )
                Calendar.getInstance().time
            } else {
                value
            }
        }

    init {
        if (EventDay.isDateInFuture(_eventDate)) {
            Log.e(
                "EventDay",
                "Member variable EVENTDAY was in the future, it is now set to current date"
            )
            _eventDate = Calendar.getInstance().time
        }
    }

    /**
     * getEventDateAsString returns the member var EVENTDATE as a formatted/localized string
     *
     * @param format : Int = DateFormat.Short
     * @param locale : Locale = Locale.getDefault()
     * @return String
     */
    fun getEventDateAsString(format: Int = DateFormat.SHORT, locale: Locale = Locale.getDefault()): String {
        return parseDateToString(this.eventDate, format, locale)
    }

    /**
     * getDaysUntil compares the current date and the member var EVENTDATE and calculates the difference in days
     *
     * @return Int
     */
    fun getDaysUntil(): Int {
        val dateInCurrentTimeContext = dateToCurrentTimeContext()
        val dayDiff = dateInCurrentTimeContext.time - EventDay.normalizedDate(Calendar.getInstance().time).time
        return (dayDiff / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * dateToCurrentTimeContext changes a past date to a current time context
     * this means, that if it is a yearly event (like a birthday) then this is going to return a date
     * with the birthdaydate but with the year changed to the coming year (if the day of the birthday already came)
     * otherwise it changes the year to the current year
     *
     * This is helper function to make it easier to process two near dates
     *
     * @return Date
     */
    private fun dateToCurrentTimeContext(): Date {
        //get instance of calender, assign the past date to it, and change year to current year
        //this is needed to check if the date is this or next year
        //this is needed for calculating how many days until the event
        val dateInCurrentTimeContext = Calendar.getInstance()
        dateInCurrentTimeContext.time = EventDay.normalizedDate(this._eventDate)
        dateInCurrentTimeContext.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))

        //if past date with current year is before current date then set year to next year
        if (dateInCurrentTimeContext.time.before(EventDay.normalizedDate(Calendar.getInstance().time))) {
            dateInCurrentTimeContext.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1)
        }
        return dateInCurrentTimeContext.time
    }

    companion object {
        /**
         * parseLocalizedDateToString parses the member variable EVENTDATE to a localized string in short format
         * @param date : Date
         * @param format : Int = DateFormat.Short
         * @param locale : Locale = Locale.getDefault()
         * @return String
         */
        @JvmStatic
        fun parseDateToString(
            date: Date,
            format: Int = DateFormat.SHORT,
            locale: Locale = Locale.getDefault()
        ): String {
            return DateFormat.getDateInstance(format, locale).format(date)
        }

        /**
         * parseStringToDate parses a string in localized short format to a date which has 00:00:00 as time
         * @param date_string : String
         * @param format : Int = DateFormat.Short
         * @param locale : Locale = Locale.getDefault()
         * @return Date
         */
        @JvmStatic
        fun parseStringToDate(
            date_string: String,
            format: Int = DateFormat.SHORT,
            locale: Locale = Locale.getDefault()
        ): Date {
            return DateFormat.getDateInstance(format, locale).parse(date_string)
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

        /**
         * normalizeDate nulls time values of a date, so all dates are equals, when they are on the same day
         *
         * @return Date
         */
        @JvmStatic
        private fun normalizedDate(date: Date): Date {
            val normalizedDateCal = Calendar.getInstance()
            normalizedDateCal.time = date
            normalizedDateCal.set(Calendar.MILLISECOND, 0)
            normalizedDateCal.set(Calendar.SECOND, 0)
            normalizedDateCal.set(Calendar.MINUTE, 0)
            normalizedDateCal.set(Calendar.HOUR, 0)
            normalizedDateCal.set(Calendar.HOUR_OF_DAY, 0)
            return normalizedDateCal.time
        }
    }
}