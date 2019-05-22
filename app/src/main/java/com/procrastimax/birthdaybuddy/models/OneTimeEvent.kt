package com.procrastimax.birthdaybuddy.models

import android.util.Log
import com.procrastimax.birthdaybuddy.handler.IOHandler
import java.text.DateFormat
import java.util.*

class OneTimeEvent(_eventdate: Date, var name: String) : EventDate(_eventdate) {

    /**
     * Identifier is an identifier for sorting
     * also used for map-like parsing for reading/writing
     */
    enum class Identifier : SortIdentifier {
        Date {
            override fun Identifier(): Int = 0
        },
        Name {
            override fun Identifier(): Int = 1
        },
        Note {
            override fun Identifier(): Int = 2
        }
    }

    var note: String? = null
        get() {
            return if (field == null) {
                null
            } else if (field!!.isEmpty() || field!!.isBlank()) {
                Log.d("OneTimeEvent", "member var NOTE is blank/empty when trying to access it")
                null
            } else {
                field!!.trim()
            }
        }
        set(value) {
            if (value == null) {
                Log.d("OneTimeEvent", "member variable NOTE was set to a null value!")
                field = null
            } else field = if (value.isBlank() || value.isEmpty()) {
                Log.d("OneTimeEvent", "member variable NOTE was set to an empty/blank value!")
                null
            } else {
                value
            }
        }

    fun getYearsUntil(): Int {
        val futureDateCal = Calendar.getInstance()
        futureDateCal.time = this.eventDate

        return (futureDateCal.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR))
    }

    /**
     * dateIsExpired returns false if the date is in the future
     * If this returns true, the event expired
     * @return Boolean
     */
    fun dateIsExpired(): Boolean {
        //function is date in future doesn't consider the case that the day of the one-time event is the current day, bc. all events are normalized
        //so when its the same day, as today, the one-time event doesn't expire, so it still appears in the list
        return getDayOfYear() == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }

    override fun toString(): String {
        return "$Name${IOHandler.characterDivider_properties}" +
                "${Identifier.Name}${IOHandler.characterDivider_values}${this.name}" +
                "${IOHandler.characterDivider_properties}${Identifier.Date}${IOHandler.characterDivider_values}${parseDateToString(
                    this.eventDate,
                    DateFormat.DEFAULT,
                    Locale.GERMAN
                )}" +
                getStringFromValue(
                    Identifier.Note,
                    this.note
                )
    }

    companion object {
        const val Name: String = "OneTimeEvent"
    }
}