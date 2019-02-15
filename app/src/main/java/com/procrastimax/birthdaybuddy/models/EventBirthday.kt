package com.procrastimax.birthdaybuddy.models

import android.util.Log
import java.text.DateFormat
import java.util.Date

/**
 * EventBirthday is model class to store basic data about a persons birthday
 *
 * It inherits from EventDay, so it uses a Date, and Strings for the name of the described person
 *
 * TODO: Add a path/link to an image of the person
 *
 *
 *  @param _birthday : Date
 *  @param _forename : String
 *  @param _surname : String
 * @author Procrastimax
 */
class EventBirthday(private var _birthday: Date, private var _forename: String, private var _surname: String) :
    EventDay(_birthday) {

    var forename: String
        get() = _forename
        set(value) {
            _forename = if (value.isBlank() || value.isEmpty()) {
                Log.e("EventBirthday", "member variable FORENAME was set to an empty/blank value!")
                "-"
            } else {
                value
            }
        }

    var surname: String
        get() = _surname
        set(value) {
            _surname = if (value.isBlank() || value.isEmpty()) {
                Log.e("EventBirthday", "member variable SURNAME was set to an empty/blank value!")
                "-"
            } else {
                value
            }
        }

    var note: String = ""
        get() {
            return if (field.isEmpty() || field.isBlank()) {
                Log.e("EventBirthday", "member var NOTE is blank/empty when trying to access it")
                "-"
            } else {
                field.trim()
            }
        }
        set(value) {
            field = if (value.isBlank() || value.isEmpty()) {
                Log.e("EventBirthday", "member variable SURNAME was set to an empty/blank value!")
                "-"
            } else {
                value
            }
        }

    init {
        if (_forename.isBlank() || _forename.isEmpty()) {
            Log.e("EventBirthday", "FORENAME is empty or blank")
            _forename = "-"
        }
        if (_surname.isBlank() || _surname.isEmpty()) {
            Log.e("EventBirthday", "SURNAME is empty or blank")
            _surname = "-"
        }
    }

    override fun toString(): String {
        return "[ Forename: ${this._forename}  Surname: ${this._surname}  Date: ${EventDay.parseDateToString(
            this.eventDate,
            DateFormat.SHORT
        )}  Note: ${this.note} ]"
    }
}