package com.procrastimax.birthdaybuddy.models

import android.util.Log
import com.procrastimax.birthdaybuddy.handler.IOHandler
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
 *  - add possibility for favorites
 *
 *  @param _birthday : Date
 *  @param _forename : String
 *  @param isYearGiven : Boolean
 * @author Procrastimax
 */
class EventBirthday(
    private var _birthday: Date,
    private var _forename: String,
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

    var surname: String? = null

    var note: String? = null

    var nickname: String? = null

    var avatarImageUri: String? = null

    fun getNicknameOrForename(): String {
        return if (this.nickname != null) {
            this.nickname!!
        } else {
            forename
        }
    }

    /**
     * TODO: only save not null member vars
     * toString returns EventBirthday as string representation
     * This is "optimized" for Serialization, so THE FIRST WORD HAS TO BE THE TYPIFICATION f.e. "Birthday"
     * returned string follows the pattern TYPE|FORENAME|SURNAME|EVENTDATE|ISYEARGIVEN|NOTE|AVATARURI|NICKNAME
     * @return String
     */
    override fun toString(): String {
        return "$Name${IOHandler.characterDivider_properties}${Identifier.Forename}${IOHandler.characterDivider_values}${this._forename}${IOHandler.characterDivider_properties}" +
                "${Identifier.Date}${IOHandler.characterDivider_values}${parseDateToString(
                    this.eventDate,
                    DateFormat.DEFAULT,
                    Locale.GERMAN
                )}" +
                "${IOHandler.characterDivider_properties}${Identifier.IsYearGiven}${IOHandler.characterDivider_values}${this.isYearGiven}" +
                "${getStringFromValue(
                    Identifier.Nickname,
                    this.nickname
                )}${getStringFromValue(
                    Identifier.Note,
                    this.note
                )}${getStringFromValue(
                    Identifier.AvatarUri,
                    this.avatarImageUri
                )}${getStringFromValue(Identifier.Nickname, this.nickname)}"
    }

    companion object {
        const val Name: String = "EventBirthday"
    }
}