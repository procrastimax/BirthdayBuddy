package com.procrastimax.birthdaybuddy.models

import android.util.Log
import com.procrastimax.birthdaybuddy.handler.IOHandler
import java.text.DateFormat
import java.util.*

/**
 * TODO:
 * - rework note workings (null)
 */
class AnnualEvent(_eventDate: Date, var name: String, var hasStartYear: Boolean) :
    EventDate(_eventDate) {

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
        HasStartYear {
            override fun Identifier(): Int = 2
        },
        Note {
            override fun Identifier(): Int = 3
        },
    }

    var note: String? = null
        get() {
            if (field == null) {
                return null
            } else if (field!!.isEmpty() || field!!.isBlank()) {
                Log.d("AnnualEvent", "member var NOTE is blank/empty when trying to access it")
                return null
            } else {
                return field!!.trim()
            }
        }
        set(value) {
            if (value == null) {
                Log.d("AnnualEvent", "member variable NOTE was set to a null value!")
                field = null
            } else field = if (value.isBlank() || value.isEmpty()) {
                Log.d("AnnualEvent", "member variable NOTE was set to an empty/blank value!")
                null
            } else {
                value
            }
        }

    /**
     * toString returns EventBirthday as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "Birthday"
     * returned string follows the pattern TYPE|FORENAME|SURNAME|EVENTDATE|ISYEARGIVEN|NOTE
     * @return String
     */
    override fun toString(): String {
        return "$Name${IOHandler.characterDivider_properties}" +
                "${Identifier.Name}${IOHandler.characterDivider_values}${this.name}" +
                "${IOHandler.characterDivider_properties}${Identifier.Date}${IOHandler.characterDivider_values}${parseDateToString(
                    this.eventDate,
                    DateFormat.DEFAULT,
                    Locale.GERMAN
                )}${IOHandler.characterDivider_properties}" +
                "${Identifier.HasStartYear}${IOHandler.characterDivider_values}${this.hasStartYear}" +
                getStringFromValue(
                    Identifier.Note,
                    this.note
                )
    }

    companion object {
        const val Name: String = "AnnualEvent"
    }
}