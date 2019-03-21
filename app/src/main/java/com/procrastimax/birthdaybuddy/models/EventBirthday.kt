package com.procrastimax.birthdaybuddy.models

import android.util.Log
import com.procrastimax.birthdaybuddy.EventDataIO
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
        AvatarUri {
            override fun Identifier(): Int = 5
        },
        Nickname {
            override fun Identifier(): Int = 6
        }
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

    var nickname: String? = null

    var avatarImageUri: String? = null

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
     * TODO: only save not null member vars
     * toString returns EventBirthday as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "Birthday"
     * returned string follows the pattern TYPE|FORENAME|SURNAME|EVENTDATE|ISYEARGIVEN|NOTE|AVATARURI|NICKNAME
     * @return String
     */
    override fun toString(): String {

        return "Birthday${EventDataIO.divider_chars_properties}${Identifier.Forename}${EventDataIO.divider_chars_values}${this._forename}${EventDataIO.divider_chars_properties}" +
                "${Identifier.Surname}${EventDataIO.divider_chars_values}${this._surname}${EventDataIO.divider_chars_properties}" +
                "${Identifier.Date}${EventDataIO.divider_chars_values}${EventDate.parseDateToString(
                    this.eventDate,
                    DateFormat.DEFAULT
                )}" +
                "${EventDataIO.divider_chars_properties}${Identifier.IsYearGiven}${EventDataIO.divider_chars_values}${this.isYearGiven}" +
                "${EventDate.getStringFromValue(
                    Identifier.Note,
                    this.note
                )}${EventDate.getStringFromValue(
                    Identifier.AvatarUri,
                    this.avatarImageUri
                )}${EventDate.getStringFromValue(Identifier.Nickname, this.nickname)}"
    }
}