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
 * @param _eventDate The date of the event
 * @author Procrastimax
 */
open class EventDate(private var _eventDate: Date) : Comparable<EventDate> {

    /**
     * compareTo is the implementation of the comparable interface
     * @param other : EventDay
     * @return Int negative for if compares instance is less than, 0 for equal and positive value if compares instance is greater than this instance
     */
    override fun compareTo(other: EventDate): Int {
        //TODO: add identifying for sorting

        val cal_l = Calendar.getInstance()
        cal_l.time = this.eventDate

        val cal_r = Calendar.getInstance()
        cal_r.time = other.eventDate

        val days_of_year_l = cal_l.get(Calendar.DAY_OF_YEAR)
        val days_of_year_r = cal_r.get(Calendar.DAY_OF_YEAR)

        //if days of year less than days of year from other instace
        if (days_of_year_l < days_of_year_r) return -1

        //if days of years are equal, check year
        else if (days_of_year_l == days_of_year_r) {
            //if years of left instance is less than years of right, return negative value (-1)
            if (cal_l.get(Calendar.YEAR) < cal_r.get(Calendar.YEAR)) {
                return -1
            } else if (cal_l.get(Calendar.YEAR) > cal_r.get(Calendar.YEAR)) {
                return 1
            } else return 0
            //else the right value has to be smaller
        } else return 1
    }

    /**
     * Identifier is an identifier for sorting
     */
    enum class Identifier : SortIdentifier {
        Date {
            override fun Identifier(): Int = 0
        }
    }

    //short "hack" to make it possible to set a getter/setter for primary constructed class members
    // eventDate is used as an alibi member
    var eventDate: Date
        get() = EventDate.normalizeDate(_eventDate)
        set(value) {
            _eventDate = if (EventDate.isDateInFuture(value)) {
                Log.d(
                    "EventDay",
                    "Member variable EVENTDAY was in the future, it is now set to current date"
                )
                EventDate.normalizeDate(Calendar.getInstance().time)
            } else {
                EventDate.normalizeDate(value)
            }
        }

    init {
        _eventDate = if (EventDate.isDateInFuture(_eventDate)) {
            Log.d(
                "EventDay",
                "Member variable EVENTDAY was in the future, it is now set to current date"
            )
            EventDate.normalizeDate(Calendar.getInstance().time)
        } else {
            EventDate.normalizeDate(_eventDate)
        }
    }

    /**
     * TODO: only save not null member vars
     * toString returns EventDay as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "EventDay"
     * returned string follows the pattern EVENTDATE
     * @return String
     */
    override fun toString(): String {
        return "EventDate||${Identifier.Date}::${EventDate.parseDateToString(
            this.eventDate,
            DateFormat.DEFAULT
        )}"
    }

    /**
     * dateToPrettyString returns a string from the member var eventdate
     * This string can be modified by format and locale
     *
     * @param format : Int = DateFormat.SHORT
     * @param locale : Locale = Locale.getDefault()
     * @return String
     */
    fun dateToPrettyString(format: Int = DateFormat.SHORT, locale: Locale = Locale.getDefault()): String {
        return parseDateToString(this.eventDate, format, locale)
    }

    /**
     * getDaysUntil compares the current date and the member var EVENTDATE and calculates the difference in days
     *
     * @return Int
     */
    fun getDaysUntil(): Int {
        val dateInCurrentTimeContext = dateToCurrentTimeContext()
        val dayDiff = dateInCurrentTimeContext.time - EventDate.normalizeDate(Calendar.getInstance().time).time
        return (dayDiff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    fun getYear(): Int {
        val cal = Calendar.getInstance()
        cal.time = this.eventDate
        return cal.get(Calendar.YEAR)
    }

    fun getMonth(): Int {
        val cal = Calendar.getInstance()
        cal.time = this.eventDate
        return cal.get(Calendar.MONTH)
    }

    fun getDayOfYear(): Int {
        val cal = Calendar.getInstance()
        cal.time = this.eventDate
        return cal.get(Calendar.DAY_OF_YEAR)
    }

    fun getDayOfMonth(): Int {
        val cal = Calendar.getInstance()
        cal.time = this.eventDate
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * getYearsSince returns the difference between the member var EVENTDATE and the current date in years
     * This function respects the case, that a date which has not occurred in the current year, is decremented by one
     *
     * @return Int
     */
    fun getYearsSince(): Int {
        val pastDateCal = Calendar.getInstance()
        pastDateCal.time = this.eventDate

        val currentCal = Calendar.getInstance()

        return if (dateToCurrentYear().before(currentCal.time)) {
            (currentCal.get(Calendar.YEAR) - pastDateCal.get(Calendar.YEAR))
        } else {
            (currentCal.get(Calendar.YEAR) - pastDateCal.get(Calendar.YEAR) - 1)
        }
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
    fun dateToCurrentTimeContext(): Date {
        //get instance of calender, assign the past date to it, and change year to current year
        //this is needed to check if the date is this or next year
        //this is needed for calculating how many days until the event
        val dateInCurrentTimeContext = Calendar.getInstance()
        dateInCurrentTimeContext.time = this.eventDate
        dateInCurrentTimeContext.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))

        //if past date with current year is before current date then set year to next year
        if (dateInCurrentTimeContext.time.before(EventDate.normalizeDate(Calendar.getInstance().time))) {
            dateInCurrentTimeContext.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1)
        }
        return dateInCurrentTimeContext.time
    }

    /**
     * dateToCurrentYear changes the year member var EVENTDATE to the current year
     * @return Date
     */
    private fun dateToCurrentYear(): Date {
        val dateInCurrentYear = Calendar.getInstance()
        dateInCurrentYear.time = this.eventDate
        dateInCurrentYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
        return dateInCurrentYear.time
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
            format: Int = DateFormat.DEFAULT,
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
            format: Int = DateFormat.DEFAULT,
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
        private fun normalizeDate(date: Date): Date {
            val normalizedDateCal = Calendar.getInstance()
            normalizedDateCal.time = date
            //set to mid day, so when sorting with monthdivider on same date, month divider are always in front
            normalizedDateCal.set(Calendar.HOUR_OF_DAY, 12)
            return normalizedDateCal.time
        }
    }
}