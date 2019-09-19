package com.procrastimax.birthdaybuddy.models

import com.procrastimax.birthdaybuddy.handler.IOHandler
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


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
open class EventDate(var eventDate: Date) : Comparable<EventDate> {

    var eventID: Int = IOHandler.getHighestIndex() + 1

    /**
     * compareTo is the implementation of the comparable interface
     * @param other : EventDay
     * @return Int negative if compared instance is less than, 0 for equal and positive value if compares instance is greater than this instance
     */
    override fun compareTo(other: EventDate): Int {
        val calL = Calendar.getInstance()
        calL.time = this.eventDate

        val calR = Calendar.getInstance()
        calR.time = other.eventDate

        //set same year for both dates, normalizes times
        calR.set(Calendar.YEAR, calL.get(Calendar.YEAR))

        calL.set(Calendar.HOUR_OF_DAY, 0)
        calL.set(Calendar.MINUTE, 0)
        calL.set(Calendar.SECOND, 0)
        calL.set(Calendar.MILLISECOND, 0)

        calR.set(Calendar.HOUR_OF_DAY, 0)
        calR.set(Calendar.MINUTE, 0)
        calR.set(Calendar.SECOND, 0)
        calR.set(Calendar.MILLISECOND, 0)

        return if (calL.time == calR.time) {
            // when equal, check if the compared one is a month divider, then rank it lower by returning 1
            if (other is MonthDivider) {
                return 1
            }

            // when equal, check if this one is a month divider, if that is the case, rank it lower by returning -1
            if (this is MonthDivider) {
                return -1
            }

            0
        } else if (calL.before(calR)) {
            -1
        } else 1
    }

    /**
     * getPrettyShortStringWithoutYear returns a localized date in very short format like 06.02 or 06/02
     * @param locale : Locale = Locale.getDefault()
     * @return String
     */
    fun getPrettyShortStringWithoutYear(locale: Locale = Locale.getDefault()): String {
        return getLocalizedDayAndMonthString(this.eventDate, locale)
    }

    /**
     * Identifier is an identifier for sorting
     */
    enum class Identifier : SortIdentifier {
        Date {
            override fun Identifier(): Int = 0
        }
    }

    /**
     * toString returns EventDay as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "EventDay"
     * returned string follows the pattern EVENTDATE
     * @return String
     */
    override fun toString(): String {
        return "$Name${getStringFromValue(
            Identifier.Date, parseDateToString(
                this.eventDate,
                DateFormat.DEFAULT,
                Locale.GERMAN
            )
        )}"
    }

    /**
     * dateToPrettyString returns a string from the member var EventDate
     * This string can be modified by format and locale
     *
     * @param format : Int = DateFormat.SHORT
     * @param locale : Locale = Locale.getDefault()
     * @return String
     */
    fun dateToPrettyString(
        format: Int = DateFormat.SHORT,
        locale: Locale = Locale.getDefault()
    ): String {
        return parseDateToString(this.eventDate, format, locale)
    }

    /**
     * getDaysUntil compares the current date and the member var EventDate and calculates the difference in days
     * @return Int
     */
    open fun getDaysUntil(): Int {

        val currentDayCal = Calendar.getInstance()
        currentDayCal.set(Calendar.HOUR_OF_DAY, 0)
        currentDayCal.set(Calendar.MINUTE, 0)
        currentDayCal.set(Calendar.SECOND, 0)

        if (!eventAlreadyOccurred()) {
            val nextYear = Calendar.getInstance()
            nextYear.time = eventDate
            nextYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))

            if (nextYear.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                nextYear.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
            ) {
                return 0
            }

            return TimeUnit.MILLISECONDS.toDays(nextYear.timeInMillis - currentDayCal.timeInMillis)
                .toInt()
        } else {
            val nextYear = Calendar.getInstance()
            nextYear.time = eventDate
            nextYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1)
            return TimeUnit.MILLISECONDS.toDays(nextYear.timeInMillis - currentDayCal.timeInMillis)
                .toInt()
        }
    }

    fun getWeeksUntilAsString(): String {
        val num: Float = getDaysUntil() / 7.0f
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(num)
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

    fun eventAlreadyOccurred(): Boolean {
        val current = Calendar.getInstance().apply {
            time = dateToCurrentYear()
        }
        return (current.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
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
        currentCal.set(Calendar.HOUR_OF_DAY, pastDateCal.get(Calendar.HOUR_OF_DAY))
        currentCal.set(Calendar.MINUTE, pastDateCal.get(Calendar.MINUTE))
        currentCal.set(Calendar.SECOND, pastDateCal.get(Calendar.SECOND))
        currentCal.set(Calendar.MILLISECOND, pastDateCal.get(Calendar.MILLISECOND))

        return if (dateToCurrentYear().before(currentCal.time)) {
            (currentCal.get(Calendar.YEAR) - pastDateCal.get(Calendar.YEAR))
        } else {
            val notOccurredDateYear =
                currentCal.get(Calendar.YEAR) - pastDateCal.get(Calendar.YEAR) - 1
            if (notOccurredDateYear < 0) {
                0
            } else {
                notOccurredDateYear
            }
        }
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
         * dateToCurrentTimeContext changes a past date to a current time context
         * this means, that if it is a yearly event (like a birthday) then this is going to return a date
         * with the birthday date but with the year changed to the coming year (if the day of the birthday already came)
         * otherwise it changes the year to the current year
         *
         * This is helper function to make it easier to process two near dates
         *
         * @return Date
         */
        fun dateToCurrentTimeContext(date: Date): Date {
            //get instance of calender, assign the past date to it, and change year to current year
            //this is needed to check if the date is this or next year
            val dateInCurrentTimeContext = Calendar.getInstance()
            dateInCurrentTimeContext.time = date
            dateInCurrentTimeContext.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))

            //if past date with current year is before current day then set year to next year
            if (dateInCurrentTimeContext.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(
                    Calendar.DAY_OF_YEAR
                )
            ) {
                dateInCurrentTimeContext.set(
                    Calendar.YEAR,
                    Calendar.getInstance().get(Calendar.YEAR) + 1
                )
            }
            dateInCurrentTimeContext.set(Calendar.HOUR_OF_DAY, 0)
            dateInCurrentTimeContext.set(Calendar.SECOND, 1)
            return dateInCurrentTimeContext.time
        }

        /**
         * parseLocalizedDateToString parses the member variable EventDate to a localized string in short format
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

        @JvmStatic
        fun parseStringToTime(
            timeString: String
        ): Date {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.parse(timeString)
        }

        @JvmStatic
        fun parseTimeToString(
            date: Date
        ): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(date)
        }

        @JvmStatic
        fun getHourFromTimeString(timeString: String): Int {
            val date = parseStringToTime(timeString)
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.get(Calendar.HOUR_OF_DAY)
        }

        @JvmStatic
        fun getMinuteFromTimeString(timeString: String): Int {
            val date = parseStringToTime(timeString)
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.get(Calendar.MINUTE)
        }

        /**
         * isDateInFuture checks if the member var eventDate is in the future (>currentDate)
         * @return Boolean
         * @param date : Date
         */
        @JvmStatic
        fun isDateInFuture(date: Date): Boolean {
            return date.after(Calendar.getInstance().time)
        }

        /**
         * getStringFromValue is a function to turn a value with an identifier to a string
         * When the value is null, like a null note in birthdays, then return null string
         */
        @JvmStatic
        fun <T> getStringFromValue(identifier: SortIdentifier, value: T): String {
            return if (value != null) {
                "${IOHandler.characterDivider_properties}$identifier${IOHandler.characterDivider_values}$value"

            } else ""
        }

        @JvmStatic
        fun getLocalizedDayAndMonthString(
            date: Date,
            locale: Locale = Locale.getDefault()
        ): String {
            val skeletonPattern = "ddMM"
            val workingFormat =
                android.text.format.DateFormat.getBestDateTimePattern(locale, skeletonPattern)
            return SimpleDateFormat(workingFormat, locale).format(date)
        }

        @JvmStatic
        fun getLocalizedDayMonthYearString(
            date: Date,
            locale: Locale = Locale.getDefault()
        ): String {
            val skeletonPattern = "ddMMYYYY"
            val workingFormat =
                android.text.format.DateFormat.getBestDateTimePattern(locale, skeletonPattern)
            return SimpleDateFormat(workingFormat, locale).format(date)
        }

        @JvmStatic
        fun getLocalizedDateFormatPatternFromSkeleton(
            skeleton: String,
            locale: Locale = Locale.getDefault()
        ): String {
            return android.text.format.DateFormat.getBestDateTimePattern(locale, skeleton)
        }

        @JvmStatic
        fun parseStringToDateWithPattern(
            pattern: String,
            dateString: String,
            locale: Locale = Locale.getDefault()
        ): Date {
            val workingFormat =
                android.text.format.DateFormat.getBestDateTimePattern(locale, pattern)
            val separatorChar = if (workingFormat.contains("-")) {
                "-"
            } else if (workingFormat.contains(".")) {
                "."
            } else {
                "/"
            }

            //replace all non digits with a unified localized sperator string
            val modifiedDateString = dateString.replace("""\W""".toRegex(), separatorChar)
            return parseStringToDate(modifiedDateString, DateFormat.DATE_FIELD)
        }

        const val Name: String = "EventDate"
    }
}