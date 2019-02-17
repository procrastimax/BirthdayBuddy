package com.procrastimax.birthdaybuddy.models

import android.util.Log
import java.text.DateFormat
import java.util.Date

/**
 * EventBirthday is model class to store basic data about a persons birthday
 *
 * It inherits from EventDay, so it uses a Date, and Strings for the name of the described person
 * Surname cant be null, if it shouldn't be set, use "0" to mark the surname as unwanted property when f.e. don't show it in UI
 * isYearGiven is flag to indicate wether the birthday-year is known/given
 *
 * TODO: Add a path/link to an image of the person
 *
 *  @param _birthday : Date
 *  @param _forename : String
 *  @param _surname : String
 *  @param isYearGiven : Boolean
 * @author Procrastimax
 */
class EventBirthday(private var _birthday: Date, private var _forename: String, private var _surname: String = "0",  var isYearGiven : Boolean = true) :
    EventDay(_birthday) {

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

    var note: String = ""
        get() {
            return if (field.isEmpty() || field.isBlank()) {
                Log.d("EventBirthday", "member var NOTE is blank/empty when trying to access it")
                "-"
            } else {
                field.trim()
            }
        }
        set(value) {
            field = if (value.isBlank() || value.isEmpty()) {
                Log.d("EventBirthday", "member variable SURNAME was set to an empty/blank value!")
                "-"
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
            Log.d("EventBirthday", "SURNAME is empty or blank")
            _surname = "-"
        }
    }

    /**
     * toString returns EventBirthday as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "Birthday"
     * returned string follows the pattern TYPE|FORENAME|SURNAME|EVENTDATE|NOTE|ISYEARGIVEN
     * @return String
     */
    override fun toString(): String {
        return "Birthday|${this._forename}|${this._surname}|${EventDay.parseDateToString(
            this.eventDate,
            DateFormat.SHORT
        )}|${this.note}|${this.isYearGiven}"
    }

    fun getMututableStringSet() : MutableSet<String>{
        return mutableSetOf(this.dateToPrettyString(),this.forename, this.forename, this.isYearGiven.toString())
    }
}