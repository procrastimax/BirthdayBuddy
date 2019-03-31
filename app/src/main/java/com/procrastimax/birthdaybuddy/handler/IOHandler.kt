package com.procrastimax.birthdaybuddy.handler

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.procrastimax.birthdaybuddy.BuildConfig
import com.procrastimax.birthdaybuddy.models.*

/**
 * DataHandler is a singleton and is used to store/read event data from shared preferences
 * It stores all data in shared preferences and the has to be initialized by getting the main context
 *
 * All events are saved as an key, value pair. In which the key is an integer value and the value is a eventday
 *
 * TODO: dont return null at any time
 */
object IOHandler {

    object SharedPrefKeys {
        const val key_firstStart = "isFirstStart"
        const val key_isNotificationOn = "isNotificationOn"
        const val key_isNotificationSoundOn = "isNotificationSoundOn"
        const val key_isNotificationVibrationOn = "isNotificationVibrationOn"
        const val key_strNotificationTime = "strNotificationTime"

        const val key_isIntervall_Month = "isIntervall_Month"
        const val key_isIntervall_Week = "isIntervall_Week"
        const val key_isIntervall_Day = "isIntervall_Day"
        const val key_isIntervall_EventDay = "isIntervall_EventDay"

        const val key_isNotification_Birthday = "isNotification_Birthday"
        const val key_isNotification_AnnualEvent = "isNotification_AnnualEvent"
        const val key_isNotification_OneTimeEvent = "isNotification_OneTimeEvent"
    }

    //Filename of shared preference to store event data and settings data
    private val fileName_eventData = BuildConfig.APPLICATION_ID + ".EventData"
    private val fileName_settings = BuildConfig.APPLICATION_ID + ".Settings"

    private lateinit var sharedPrefEventData: SharedPreferences
    private lateinit var sharedPrefSettings: SharedPreferences

    const val characterDivider_properties = "||"
    const val characterDivider_values = "::"

    /**
     * registerIO has to be called before any io writing/reading is done
     * This function has to get the main context to use shared preferences
     *
     * @param context : Context
     */
    fun registerIO(context: Context) {
        sharedPrefEventData = context.getSharedPreferences(fileName_eventData, Context.MODE_PRIVATE)
        sharedPrefSettings = context.getSharedPreferences(fileName_settings, Context.MODE_PRIVATE)
    }

    fun initializeAllSettings() {
        //notifications on
        writeSetting(SharedPrefKeys.key_isNotificationOn, true)

        //notification sound on
        writeSetting(SharedPrefKeys.key_isNotificationSoundOn, true)

        //notification vibration on
        writeSetting(SharedPrefKeys.key_isNotificationSoundOn, false)

        //notification time to 12:00
        writeSetting(SharedPrefKeys.key_strNotificationTime, "12:00")

        //notification intervalls
        writeSetting(SharedPrefKeys.key_isIntervall_Month, false)
        writeSetting(SharedPrefKeys.key_isIntervall_Week, false)
        writeSetting(SharedPrefKeys.key_isIntervall_Day, true)
        writeSetting(SharedPrefKeys.key_isIntervall_EventDay, true)

        //notification vibration on
        writeSetting(SharedPrefKeys.key_isNotification_Birthday, true)
        writeSetting(SharedPrefKeys.key_isNotification_AnnualEvent, true)
        writeSetting(SharedPrefKeys.key_isNotification_OneTimeEvent, true)
    }

    fun settingsContainsKey(key: String): Boolean {
        return (sharedPrefSettings.contains(key))
    }

    fun <T> writeSetting(key: String, value: T) {
        //only try to write when the type matches
        val editor = sharedPrefSettings.edit()
        when (value) {
            is String -> {
                editor.putString(key, value as String)
            }
            is Boolean -> {
                editor.putBoolean(key, value as Boolean)
            }
            is Int -> {
                editor.putInt(key, value as Int)
            }
            is Float -> {
                editor.putFloat(key, value as Float)
            }
        }
        editor.apply()
    }

    fun getStringFromKey(key: String): String? {
        return sharedPrefSettings.getString(key, null)
    }

    fun getIntFromKey(key: String): Int? {
        if (settingsContainsKey(key)) {
            return sharedPrefSettings.getInt(key, -1)
        } else {
            return null
        }
    }

    fun getBooleanFromKey(key: String): Boolean? {
        if (settingsContainsKey(key)) {
            return sharedPrefSettings.getBoolean(key, false)
        } else {
            return null
        }
    }

    fun getFloatFromKey(key: String): Float? {
        if (settingsContainsKey(key)) {
            return sharedPrefSettings.getFloat(key, -1.0f)
        } else {
            return null
        }
    }

    fun eventDataContainsKey(key: String): Boolean {
        return (sharedPrefEventData.contains(key))
    }

    fun isFirstStart(): Boolean {
        //when the key doesnt exist -> its the first start, so we have to invert the contains function
        if (!settingsContainsKey(SharedPrefKeys.key_firstStart)) {
            Log.i("IOHandler", "shared pref files didnt exist before")
            val sharedPrefEditor = sharedPrefSettings.edit()
            sharedPrefEditor.putBoolean(SharedPrefKeys.key_firstStart, false)
            sharedPrefEditor.apply()
            return true
        } else return false
    }

    /**
     * writeEventToFile writes a single event with a key to the shared preferences
     * @param key : Int
     * @param event : EventDay
     */
    fun writeEventToFile(key: Int, event: EventDate) {
        val sharedPrefEditor = sharedPrefEventData.edit()
        sharedPrefEditor.putString(key.toString(), event.toString())
        sharedPrefEditor.apply()
    }

    /**
     * removeEventFromFile removes an entry fro the shared preferences by using the key
     * @param key : Int
     */
    fun removeEventFromFile(key: Int) {
        val sharedPrefEditor = sharedPrefEventData.edit()
        sharedPrefEditor.remove(key.toString())
        sharedPrefEditor.apply()
    }

    /**
     * readEntryFromFile reads a single event entry by key
     *
     * @param key: Int
     * @return EventDay?
     */
    fun readEventFromFile(key: Int): EventDate? {
        if (sharedPrefEventData.contains(key.toString())) {
            val eventday: String? = sharedPrefEventData.getString(key.toString(), "")
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
        val sharedPrefEdit = sharedPrefEventData.edit()
        sharedPrefEdit.clear()

        for (i in 0 until EventHandler.getList().size) {
            sharedPrefEdit.putString(i.toString(), EventHandler.getList()[i].toString())

        }
        sharedPrefEdit.apply()
    }

    /**
     * getHighestIndex returns the max index value of shared pref keys
     */
    fun getHighestIndex(): Int {
        var highest = 0
        sharedPrefEventData.all.keys.forEach {
            if (it.toInt() > highest) {
                highest = it.toInt()
            }
        }
        return highest
    }

    /**
     * readAll reads all shared preferences and returns them as a Map<Int, EventDay>
     *
     * @return Map<Int, EventDay>
     */
    fun readAll(context: Context) {
        sharedPrefEventData.all.forEach {
            if (!isFirstStart()) {
                if (it.value is String) {
                    var event =
                        convertStringToEventDate(it.value as String)
                    event!!.eventID = it.key.toInt()

                    //check for onetimeevents
                    if (event is OneTimeEvent) {
                        //when onetimeevent expired, remove from shared prefs and null it, so it doesnt get added in the map
                        if (event.dateIsExpired()) {
                            sharedPrefEventData.edit().remove(it.key).apply()
                            event = null
                        }
                    }
                    if (event != null) {
                        EventHandler.addEvent(event, context)
                    }
                }
            }
        }
    }

    /**
     * convertStringToEventDay reads an string and returns a object of base class EventDay
     * It can return derived types from EventDay with the typification string at the start of every string
     *  TODO:
     *      -> do parsing with identifiers on a more dynamic way
     * @param objectString : String
     * @return EventDay?
     */
    private fun convertStringToEventDate(objectString: String): EventDate? {
        val string_array = objectString.split(characterDivider_properties)

        // BIRTHDAY EVENT
        if (string_array[0] == EventBirthday.Name) {

            var forename: String = "-"
            var surname: String = "-"
            var date: String = "-"
            var note: String? = null
            var isyeargiven: Boolean = false
            var avatarImageURI: String? = null
            var nickname: String? = null

            for (i in 1 until string_array.size) {
                val property = string_array[i].split(characterDivider_values)

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
                    EventBirthday.Identifier.AvatarUri.toString() -> {
                        avatarImageURI = property[1]
                    }
                    EventBirthday.Identifier.Nickname.toString() -> {
                        nickname = property[1]
                    }
                    else ->
                        Log.w("IOHandler", "Could not find identifier when trying to parse EventBirthday")
                }
            }

            val birthday = EventBirthday(EventDate.parseStringToDate(date), forename, surname, isyeargiven)
            if (note != null) birthday.note = note
            if (avatarImageURI != null) birthday.avatarImageUri = avatarImageURI
            if (nickname != null) birthday.nickname = nickname
            return birthday

            // MONTH DIVIDER
        } else if (string_array[0] == MonthDivider.Name) {

            var date: String = "-"
            var month: String = "-"

            for (i in 1 until string_array.size) {
                val property = string_array[i].split(characterDivider_values)

                //use identifier
                when (property[0]) {
                    MonthDivider.Identifier.Date.toString() -> {
                        date = property[1]
                    }
                    MonthDivider.Identifier.MonthName.toString() -> {
                        month = property[1]
                    }
                    else ->
                        Log.w("IOHandler", "Could not find identifier when trying to parse EventMonthDivider")
                }
            }
            return MonthDivider(EventDate.parseStringToDate(date), month)

            // ANNUAL EVENT
        } else if (string_array[0] == AnnualEvent.Name) {
            var date = "-"
            var name = "-"
            var note: String? = null
            var hasStartYear = false

            for (i in 1 until string_array.size) {
                val property = string_array[i].split(characterDivider_values)

                //use identifier
                when (property[0]) {
                    AnnualEvent.Identifier.Date.toString() -> {
                        date = property[1]
                    }
                    AnnualEvent.Identifier.Name.toString() -> {
                        name = property[1]
                    }
                    AnnualEvent.Identifier.HasStartYear.toString() -> {
                        hasStartYear = property[1].toBoolean()
                    }
                    AnnualEvent.Identifier.Note.toString() -> {
                        note = property[1]
                    }
                    else ->
                        Log.w("IOHandler", "Could not find identifier when trying to parse AnnualEvent")
                }
            }
            val anniversary = AnnualEvent(EventDate.parseStringToDate(date), name, hasStartYear)
            if (note != null) {
                anniversary.note = note
            }
            return anniversary

            // ONE TIME EVENT
        } else if (string_array[0] == OneTimeEvent.Name) {
            var date = "-"
            var name = "-"
            var note: String? = null

            for (i in 1 until string_array.size) {
                val property = string_array[i].split(characterDivider_values)

                //use identifier
                when (property[0]) {
                    OneTimeEvent.Identifier.Date.toString() -> {
                        date = property[1]
                    }
                    OneTimeEvent.Identifier.Name.toString() -> {
                        name = property[1]
                    }
                    OneTimeEvent.Identifier.Note.toString() -> {
                        note = property[1]
                    }
                    else ->
                        Log.w("IOHandler", "Could not find identifier when trying to parse OneTimeEvent")
                }
            }
            val oneTimeEvent = OneTimeEvent(EventDate.parseStringToDate(date), name)
            if (note != null) {
                oneTimeEvent.note = note
            }
            return oneTimeEvent
        }
        return null
    }
}