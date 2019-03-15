package com.procrastimax.birthdaybuddy.models

import android.util.Log
import java.text.DateFormat
import java.util.*

/**
 * TODO:
 * - rework note workings (null)
 */
class EventAnniversary(private var _eventDate: Date, private var _name: String, private var hasStartYear: Boolean) :
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

    var name: String
        get() = _name
        set(value) {
            _name = if (value.isBlank() || value.isEmpty()) {
                Log.d("EventAnniversary", "member variable NAME was set to an empty/blank value!")
                "-"
            } else {
                value
            }
        }

    var note: String? = null
        get() {
            if (field == null) {
                return null
            } else if (field!!.isEmpty() || field!!.isBlank()) {
                Log.d("EventAnniversary", "member var NOTE is blank/empty when trying to access it")
                return null
            } else {
                return field!!.trim()
            }
        }
        set(value) {
            if (value == null) {
                Log.d("EventAnniversary", "member variable NOTE was set to a null value!")
                field = null
            } else field = if (value.isBlank() || value.isEmpty()) {
                Log.d("EventAnniversary", "member variable NOTE was set to an empty/blank value!")
                null
            } else {
                value
            }
        }

    /**
     * getPrettyShortStringWithoutYear returns a localized date in very short format like 06.02 or 06/02
     * TODO: dont do it this way, get default locale date seperation symbol
     * @param locale : Locale = Locale.getDefault()
     * @return String
     */
    fun getPrettyShortStringWithoutYear(locale: Locale = Locale.getDefault()): String {
        return this.dateToPrettyString(DateFormat.SHORT, locale).substring(0..4)
    }

    /**
     * toString returns EventBirthday as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "Birthday"
     * returned string follows the pattern TYPE|FORENAME|SURNAME|EVENTDATE|NOTE|ISYEARGIVEN
     * @return String
     */
    override fun toString(): String {
        return "Anniversary|${Identifier.Name}::${this._name}|${Identifier.Date}::${EventDate.parseDateToString(
            this.eventDate,
            DateFormat.SHORT
        )}|${Identifier.Note}::${this.note}|${Identifier.HasStartYear}::${this.hasStartYear}"
    }
}