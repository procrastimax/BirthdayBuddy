package com.procrastimax.birthdaybuddy

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventAnniversary
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.MonthDivider

/**
 * DataHandler is a singleton and is used to store/read event data from shared preferences
 * It stores all data in shared preferences and the has to be initialized by getting the main context
 *
 * All events are saved as an key, value pair. In which the key is an integer value and the value is a eventday
 *
 * TODO: dont return null at any time
 */
object EventDataIO {

    //Filename of shared preference to store event data
    private val fileName = BuildConfig.APPLICATION_ID + ".EventData"

    //Key to handle wether the application already was launched or if the preference file is present
    private val preferenceInitString = "wasLaunchedBefore"

    private lateinit var sharedPref: SharedPreferences

    //Identifier string for types of Birthday
    private val type_birthday: String = "Birthday"
    private val type_month_divider: String = "MonthDivider"
    private val type_anniversary: String = "Anniversary"

    /**
     * registerIO has to be called before any io writing/reading is done
     * This function has to get the main context to use shared preferences
     *
     * @param context : Context
     */
    fun registerIO(context: Context) {
        sharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        if (!sharedPref.contains("wasLaunchedBefore")) {
            Log.d("EventDataIO", "shared pref files didnt exist before")
            val sharedPrefEditor = sharedPref.edit()
            sharedPrefEditor.putBoolean(preferenceInitString, true)
            sharedPrefEditor.apply()
        }
    }

    /**
     * writeEventToFile writes a single event with a key to the shared preferences
     * @param key : Int
     * @param event : EventDay
     */
    fun writeEventToFile(key: Int, event: EventDate) {
        val sharedPrefEditor = sharedPref.edit()
        sharedPrefEditor.putString(key.toString(), event.toString())
        sharedPrefEditor.apply()
    }

    /**
     * removeEventFromFile removes an entry fro the shared preferences by using the key
     * @param key : Int
     */
    fun removeEventFromFile(key: Int) {
        val sharedPrefEditor = sharedPref.edit()
        sharedPrefEditor.remove(key.toString())
        sharedPrefEditor.apply()
    }

    /**
     * readEntryFromFile reads a single event entry by key
     *
     * @param key: Int
     * @return EventDay?
     */
    fun readEntryFromFile(key: Int): EventDate? {
        if (sharedPref.contains(key.toString())) {
            val eventday: String? = sharedPref.getString(key.toString(), "")
            if (!eventday.isNullOrEmpty()) {
                return convertStringToEventDate(eventday)
            } else {
                return null
            }
        }
        return null
    }

    /**
     * writeAll writes all events which are currently stored in the EventHandler-Map to the shared preferences
     */
    fun writeAll() {
        val sharedPrefEdit = sharedPref.edit()
        sharedPrefEdit.clear()
        sharedPrefEdit.putBoolean(preferenceInitString, true)

        EventHandler.getEvents().forEach {
            sharedPrefEdit.putString(it.key.toString(), it.value.toString())
        }
        sharedPrefEdit.apply()
    }

    /**
     * readAll reads all shared preferences and returns them as a Map<Int, EventDay>
     *
     * @return Map<Int, EventDay>
     */
    fun readAll(): Map<Int, EventDate> {
        val tempMap: MutableMap<Int, EventDate> = emptyMap<Int, EventDate>().toMutableMap()
        sharedPref.all.forEach {
            if (!it.key.equals(preferenceInitString)) {
                if (it.value is String) {
                    val event = convertStringToEventDate(it.value as String)
                    if (event != null) {
                        tempMap[it.key.toInt()] = event
                    }
                }
            }
        }
        return tempMap
    }

    /**
     * convertStringToEventDay reads an string and returns a object of base class EventDay
     * It can return derived types from EventDay with the typification string at the start of every string
     *
     * @param objectString : String
     * @return EventDay?
     */
    private fun convertStringToEventDate(objectString: String): EventDate? {
        val string_array = objectString.split("|")

        if (string_array[0] == type_birthday) {

            var forename: String = "-"
            var surname: String = "-"
            var date: String = "-"
            var note: String = "-"
            var isyeargiven: Boolean = false

            for (i in 1 until string_array.size) {
                val property = string_array[i].split("::")

                //use identifier
                when (property[0]) {
                    EventBirthday.Identifier.Date.toString() -> {
                        date = property[1]
                    }
                    EventBirthday.Identifier.Forename.toString() -> {
                        forename = property[1]
                    }
                    EventBirthday.Identifier.Surname.toString() -> {
                        surname = property[1]
                    }
                    EventBirthday.Identifier.Note.toString() -> {
                        note = property[1]
                    }
                    EventBirthday.Identifier.IsYearGiven.toString() -> {
                        isyeargiven = property[1].toBoolean()
                    }
                    else ->
                        Log.w("EventDataIO", "Could not find identifier when trying to parse EventBirthday")
                }
            }

            val birthday = EventBirthday(EventDate.parseStringToDate(date), forename, surname, isyeargiven)
            if (note != "null") birthday.note = note
            return birthday

        } else if (string_array[0] == type_month_divider) {

            var date: String = "-"
            var month: String = "-"

            for (i in 1 until string_array.size) {
                val property = string_array[i].split("::")

                //use identifier
                when (property[0]) {
                    MonthDivider.Identifier.Date.toString() -> {
                        date = property[1]
                    }
                    MonthDivider.Identifier.MonthName.toString() -> {
                        month = property[1]
                    }
                    else ->
                        Log.w("EventDataIO", "Could not find identifier when trying to parse EventMonthDivider")
                }
            }
            return MonthDivider(EventDate.parseStringToDate(date), month)

        } else if (string_array[0] == type_anniversary) {
            var date = "-"
            var name = "-"
            var note = "null"
            var hasStartYear = false

            for (i in 1 until string_array.size) {
                val property = string_array[i].split("::")

                //use identifier
                when (property[0]) {
                    EventAnniversary.Identifier.Date.toString() -> {
                        date = property[1]
                    }
                    EventAnniversary.Identifier.Name.toString() -> {
                        name = property[1]
                    }
                    EventAnniversary.Identifier.HasStartYear.toString() -> {
                        hasStartYear = property[1].toBoolean()
                    }
                    EventAnniversary.Identifier.Note.toString() -> {
                        note = property[1]
                    }
                    else ->
                        Log.w("EventDataIO", "Could not find identifier when trying to parse EventAnniversary")
                }
            }
            val anniversary = EventAnniversary(EventDate.parseStringToDate(date), name, hasStartYear)
            if (note != "null") {
                anniversary.note = note
            }
            return anniversary
        }
        return null
    }
}