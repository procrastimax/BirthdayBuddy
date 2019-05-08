package com.procrastimax.birthdaybuddy.handler

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.procrastimax.birthdaybuddy.BuildConfig
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.models.*
import java.util.*

/**
 * DataHandler is a singleton and is used to store/read event data from shared preferences
 * It stores all data in shared preferences and the has to be initialized by getting the main context
 *
 * All events are saved as an key, value pair. In which the key is an integer value and the value is a EventDate
 */
object IOHandler {

    object SharedPrefKeys {
        const val key_firstStart = "isFirstStart"

        //birthday settings
        const val key_isNotificationOnBirthday = "isNotificationOnBirthday"
        const val key_isNotificationSoundOnBirthday = "isNotificationSoundOnBirthday"
        const val key_isNotificationVibrationOnBirthday = "isNotificationVibrationOnBirthday"
        const val key_strNotificationTimeBirthday = "strNotificationTimeBirthday"

        const val key_isRemindedDay_month_beforeBirthday = "isRemindedDay_month_beforeBirthday"
        const val key_isRemindedDay_week_beforeBirthday = "isRemindedDay_week_beforeBirthday"
        const val key_isRemindedDay_day_beforeBirthday = "isRemindedDay_day_beforeBirthday"
        const val key_isRemindedDay_eventdayBirthday = "isRemindedDay_eventdayBirthday"

        const val key_notificationLightBirthday = "notificationLightBirthday"

        //annual event settings
        const val key_isNotificationOnAnnual = "isNotificationOnAnnual"
        const val key_isNotificationSoundOnAnnual = "isNotificationSoundOnAnnual"
        const val key_isNotificationVibrationOnAnnual = "isNotificationVibrationOnAnnual"
        const val key_strNotificationTimeAnnual = "strNotificationTimeAnnual"

        const val key_isRemindedDay_month_beforeAnnual = "isRemindedDay_month_beforeAnnual"
        const val key_isRemindedDay_week_beforeAnnual = "isRemindedDay_week_beforeAnnual"
        const val key_isRemindedDay_day_beforeAnnual = "isRemindedDay_day_beforeAnnual"
        const val key_isRemindedDay_eventdayAnnual = "isRemindedDay_eventdayAnnual"

        const val key_notificationLightAnnual = "notificationLightAnnual"

        // one time event settings
        const val key_isNotificationOnOneTime = "isNotificationOnOneTime"
        const val key_isNotificationSoundOnOneTime = "isNotificationSoundOnOneTime"
        const val key_isNotificationVibrationOnOneTime = "isNotificationVibrationOnOneTime"
        const val key_strNotificationTimeOneTime = "strNotificationTimeOneTime"

        const val key_isRemindedDay_month_beforeOneTime = "isRemindedDay_month_beforeOneTime"
        const val key_isRemindedDay_week_beforeOneTime = "isRemindedDay_week_beforeOneTime"
        const val key_isRemindedDay_day_beforeOneTime = "isRemindedDay_day_beforeOneTime"
        const val key_isRemindedDay_eventdayOneTime = "isRemindedDay_eventdayOneTime"

        const val key_notificationLightOneTime = "notificationLightOneTime"
    }

    //Filename of shared preference to store event data and settings data
    private const val fileNameEventData = BuildConfig.APPLICATION_ID + ".EventData"
    private const val fileNameSettings = BuildConfig.APPLICATION_ID + ".Settings"

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
        sharedPrefEventData = context.getSharedPreferences(fileNameEventData, Context.MODE_PRIVATE)
        sharedPrefSettings = context.getSharedPreferences(fileNameSettings, Context.MODE_PRIVATE)
    }

    fun initializeAllSettings() {
        //notifications on
        writeSetting(SharedPrefKeys.key_isNotificationOnBirthday, true)
        writeSetting(SharedPrefKeys.key_isNotificationOnAnnual, true)
        writeSetting(SharedPrefKeys.key_isNotificationOnOneTime, true)

        //notification sound off
        writeSetting(SharedPrefKeys.key_isNotificationSoundOnBirthday, false)
        writeSetting(SharedPrefKeys.key_isNotificationSoundOnAnnual, false)
        writeSetting(SharedPrefKeys.key_isNotificationSoundOnOneTime, false)

        //notification vibration on
        writeSetting(SharedPrefKeys.key_isNotificationVibrationOnBirthday, true)
        writeSetting(SharedPrefKeys.key_isNotificationVibrationOnAnnual, true)
        writeSetting(SharedPrefKeys.key_isNotificationVibrationOnOneTime, true)

        //notification time to 12:00
        writeSetting(SharedPrefKeys.key_strNotificationTimeBirthday, "12:00")
        writeSetting(SharedPrefKeys.key_strNotificationTimeAnnual, "12:00")
        writeSetting(SharedPrefKeys.key_strNotificationTimeOneTime, "12:00")

        //notification reminder days
        writeSetting(SharedPrefKeys.key_isRemindedDay_month_beforeBirthday, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_week_beforeBirthday, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_day_beforeBirthday, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_eventdayBirthday, true)

        writeSetting(SharedPrefKeys.key_isRemindedDay_month_beforeAnnual, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_week_beforeAnnual, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_day_beforeAnnual, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_eventdayAnnual, true)

        writeSetting(SharedPrefKeys.key_isRemindedDay_month_beforeOneTime, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_week_beforeOneTime, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_day_beforeOneTime, true)
        writeSetting(SharedPrefKeys.key_isRemindedDay_eventdayOneTime, true)

        //notification lights to 1
        writeSetting(SharedPrefKeys.key_notificationLightBirthday, 1)
        writeSetting(SharedPrefKeys.key_notificationLightAnnual, 1)
        writeSetting(SharedPrefKeys.key_notificationLightOneTime, 1)
    }

    private fun settingsContainsKey(key: String): Boolean {
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
        return if (::sharedPrefSettings.isInitialized) {
            sharedPrefSettings.getString(key, null)
        } else null
    }

    fun getIntFromKey(key: String): Int? {
        return if (settingsContainsKey(key)) {
            sharedPrefSettings.getInt(key, -1)
        } else {
            null
        }
    }

    fun getBooleanFromKey(key: String): Boolean? {
        return if (settingsContainsKey(key)) {
            sharedPrefSettings.getBoolean(key, false)
        } else {
            null
        }
    }

    fun eventDataContainsKey(key: String): Boolean {
        return (sharedPrefEventData.contains(key))
    }

    fun isFirstStart(): Boolean {
        //when the key doesnt exist -> its the first start, so we have to invert the contains function
        return if (!settingsContainsKey(SharedPrefKeys.key_firstStart)) {
            Log.i("IOHandler", "shared pref files didnt exist before")
            val sharedPrefEditor = sharedPrefSettings.edit()
            sharedPrefEditor.putBoolean(SharedPrefKeys.key_firstStart, false)
            sharedPrefEditor.apply()
            true
        } else false
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
    fun readEventFromFile(context: Context, key: Int): EventDate? {
        if (sharedPrefEventData.contains(key.toString())) {
            val eventday: String? = sharedPrefEventData.getString(key.toString(), "")
            if (!eventday.isNullOrEmpty()) {
                return convertStringToEventDate(context, eventday)
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

    fun clearSharedPrefEventData() {
        val sharedPrefEdit = sharedPrefEventData.edit()
        sharedPrefEdit.clear()
        sharedPrefEdit.apply()
    }

    /**
     * getHighestIndex returns the max index value of shared pref keys
     */
    fun getHighestIndex(): Int {
        var highest = 0
        if (::sharedPrefEventData.isInitialized) {
            sharedPrefEventData.all.keys.forEach {
                if (it.toInt() > highest) {
                    highest = it.toInt()
                }
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
        var eventCounter = 0
        sharedPrefEventData.all.forEach {
            if (!isFirstStart()) {
                if (it.value is String) {
                    eventCounter++
                    var event =
                        convertStringToEventDate(context, it.value as String)
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
                        //when iterator is last element, add new element and update eventlist
                        if (eventCounter == sharedPrefEventData.all.size) {
                            EventHandler.addEvent(
                                event,
                                context,
                                writeAfterAdd = false,
                                addNewNotification = false,
                                updateEventList = true,
                                addBitmap = false
                            )
                        } else {
                            EventHandler.addEvent(
                                event,
                                context,
                                writeAfterAdd = false,
                                addNewNotification = false,
                                updateEventList = false,
                                addBitmap = false
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * convertStringToEventDay reads an string and returns a object of base class EventDay
     * It can return derived types from EventDay with the typification string at the start of every string
     * @param context : Context
     * @param objectString : String
     * @return EventDay?
     */
    fun convertStringToEventDate(context: Context, objectString: String): EventDate? {
        objectString.split(characterDivider_properties).let { stringArray ->
            if (stringArray.isNotEmpty()) {
                when (stringArray[0]) {
                    //BIRTHDAY EVENT PARSING
                    (EventBirthday.Name) -> {
                        var forename = "-"
                        var date = "-"
                        var note: String? = null
                        var isyeargiven = false
                        var avatarImageURI: String? = null
                        var surname: String? = null
                        var nickname: String? = null

                        for (i in 1 until stringArray.size) {
                            val property = stringArray[i].split(characterDivider_values)

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

                        val birthday =
                            EventBirthday(
                                EventDate.parseStringToDate(date, locale = Locale.GERMAN),
                                forename,
                                isyeargiven
                            )
                        if (surname != null) birthday.surname = surname
                        if (note != null) birthday.note = note
                        if (avatarImageURI != null) birthday.avatarImageUri = avatarImageURI
                        if (nickname != null) birthday.nickname = nickname
                        return birthday
                    }
                    //ANNUAL EVENT PARSING
                    (AnnualEvent.Name) -> {
                        var date = "-"
                        var name = "-"
                        var note: String? = null
                        var hasStartYear = false

                        for (i in 1 until stringArray.size) {
                            val property = stringArray[i].split(characterDivider_values)

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
                        val anniversary =
                            AnnualEvent(EventDate.parseStringToDate(date, locale = Locale.GERMAN), name, hasStartYear)
                        if (note != null) {
                            anniversary.note = note
                        }
                        return anniversary
                    }
                    //ONETIME EVENT PARSING
                    (OneTimeEvent.Name) -> {
                        var date = "-"
                        var name = "-"
                        var note: String? = null

                        for (i in 1 until stringArray.size) {
                            val property = stringArray[i].split(characterDivider_values)

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
                        val oneTimeEvent = OneTimeEvent(EventDate.parseStringToDate(date, locale = Locale.GERMAN), name)
                        if (note != null) {
                            oneTimeEvent.note = note
                        }
                        return oneTimeEvent
                    }
                    //MONTHDIVIDER EVENT PARSING
                    (MonthDivider.Name) -> {
                        var date = "-"
                        var month = "-"

                        for (i in 1 until stringArray.size) {
                            val property = stringArray[i].split(characterDivider_values)

                            //use identifier
                            when (property[0]) {
                                MonthDivider.Identifier.Date.toString() -> {
                                    date = property[1]
                                }
                                MonthDivider.Identifier.MonthName.toString() -> {
                                    val cal = Calendar.getInstance()
                                    cal.time = EventDate.parseStringToDate(date, locale = Locale.GERMAN)
                                    context.resources.getStringArray(R.array.month_names)[cal.get(Calendar.MONTH)]
                                    month = property[1]
                                }
                                else ->
                                    Log.w(
                                        "IOHandler",
                                        "Could not find identifier when trying to parse EventMonthDivider"
                                    )
                            }
                        }
                        return MonthDivider(EventDate.parseStringToDate(date, locale = Locale.GERMAN), month)
                    }
                    else -> {
                        return null
                    }
                }
            } else {
                return null
            }
        }
    }
}