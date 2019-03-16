package com.procrastimax.birthdaybuddy.models

import android.util.Log
import java.text.DateFormat
import java.util.*

/**
 * EventBirthday is model class to store basic data about a persons birthday
 *
 * It inherits from EventDay, so it uses a Date, and Strings for the name of the described person
 * Surname cant be null, if it shouldn't be set, use "0" to mark the surname as unwanted property when f.e. don't show it in UI
 * isYearGiven is flag to indicate wether the birthday-year is known/given
 *
 * TODO:
 *  - Add a path/link to an image of the person
 *  - what to do with unset note value
 *  - add possibility for nicknames
 *  - add possibility for favorites
 *  - rework note working
 *  - function to return forename or nickname
 *
 *  @param _birthday : Date
 *  @param _forename : String
 *  @param _surname : String
 *  @param isYearGiven : Boolean
 * @author Procrastimax
 */
class EventBirthday(
    private var _birthday: Date,
    private var _forename: String,
    private var _surname: String,
    var isYearGiven: Boolean = true
) :
    EventDate(_birthday) {

    /**
     * Identifier is an identifier for sorting
     */
    enum class Identifier : SortIdentifier {
        Date {
            override fun Identifier(): Int = 0
        },
        Forename {
            override fun Identifier(): Int = 1
        },
        Surname {
            override fun Identifier(): Int = 2
        },
        IsYearGiven {
            override fun Identifier(): Int = 3
        },
        Note {
            override fun Identifier(): Int = 4
        },
    }

    var forename: String
        get() = _forename
        set(value) {
            _forename = if (value.isBlank() || value.isEmpty()) {
                Log.d("EventBirthday", "member variable FORENAME was set to an empty/blank value!")
                "-"
            } else {
                value
            }
        }

    var surname: String?
        get() = _surname
        set(value) {
            _surname = if (value.isNullOrBlank() || value.isEmpty()) {
                Log.d("EventBirthday", "member variable SURNAME was set to an empty/blank value!")
                "0"
            } else {
                value
            }
        }

    var note: String? = null
        get() {
            if (field == null) {
                return null
            } else if (field!!.isEmpty() || field!!.isBlank()) {
                Log.d("EventBirthday", "member var NOTE is blank/empty when trying to access it")
                return null
            } else {
                return field!!.trim()
            }
        }
        set(value) {
            if (value == null) {
                Log.d("EventBirthday", "member variable NOTE was set to a null value!")
                field = null
            } else field = if (value.isBlank() || value.isEmpty()) {
                Log.d("EventBirthday", "member variable NOTE was set to an empty/blank value!")
                null
            } else {
                value
            }
        }

    init {
        if (_forename.isBlank() || _forename.isEmpty()) {
            Log.d("EventBirthday", "FORENAME is empty or blank")
            _forename = "-"
        }
        if (_surname.isBlank() || _surname.isEmpty()) {
            Log.d("EventBirthday", "SURNAMSE is empty or blank")
            _surname = "-"
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
        return "Birthday|${Identifier.Forename}::${this._forename}|${Identifier.Surname}::${this._surname}|${Identifier.Date}::${EventDate.parseDateToString(
            this.eventDate,
            DateFormat.SHORT
        )}|${Identifier.Note}::${this.note}|${Identifier.IsYearGiven}::${this.isYearGiven}"
    }
}