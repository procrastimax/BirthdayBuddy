package com.procrastimax.birthdaybuddy

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import com.procrastimax.birthdaybuddy.models.EventHandler

/**
 * DataHandler is a singleton and is used to store/read event data from shared preferences
 * It stores all data in shared preferences and the has to be initialized by getting the main context
 *
 * All events are saved as an key, value pair. In which the key is an integer value and the value is a eventday
 *
 * TODO: maybe use this directly with the EventHandler
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

    /**
     * registerIO has to be called before any io writing/reading is done
     * This function has to get the main context to use shared preferences
     *
     * @param context : Context
     */
    fun registerIO(context: Context) {
        //TODO: change context mode
        sharedPref = context.getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS)

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
    fun writeEventToFile(key: Int, event: EventDay) {
        val sharedPrefEditor = sharedPref.edit()
        sharedPrefEditor.putString(key.toString(), event.toString())
        sharedPrefEditor.apply()
    }

    /**
     * readEntryFromFile reads a single event entry by key
     *
     * @param key: Int
     * @return EventDay?
     */
    fun readEntryFromFile(key: Int): EventDay? {
        if (sharedPref.contains(key.toString())) {
            val eventday: String? = sharedPref.getString(key.toString(), "")
            if (!eventday.isNullOrEmpty()) {
                return convertStringToEventDay(eventday)
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
    fun readAll(): Map<Int, EventDay> {
        val tempMap: MutableMap<Int, EventDay> = emptyMap<Int, EventDay>().toMutableMap()
        sharedPref.all.forEach {
            if (!it.key.equals(preferenceInitString)) {
                if (it.value is String) {
                    val event = convertStringToEventDay(it.value as String)
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
    private fun convertStringToEventDay(objectString: String): EventDay? {
        val string_array = objectString.split("|")

        if (string_array[0].equals(type_birthday)) {
            val forename = string_array[1]
            val surname = string_array[2]
            val date = string_array[3]
            val note = string_array[4]
            val isyeargiven = string_array[5]

            val birthday = EventBirthday(EventDay.parseStringToDate(date), forename, surname, isyeargiven.toBoolean())
            birthday.note = note
            return birthday
        } else {
            return null
        }
    }
}