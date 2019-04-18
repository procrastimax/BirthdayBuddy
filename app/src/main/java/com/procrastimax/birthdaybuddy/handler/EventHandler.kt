package com.procrastimax.birthdaybuddy.handler

import android.content.Context
import android.net.Uri
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.MonthDivider
import com.procrastimax.birthdaybuddy.models.SortIdentifier
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*

/**
 * EventHandler singleton object map to store all occurring eventdates (birthdays, anniversaries, etc.)
 * This is useful to compare all objects more easily, f.e. when you want to traverse all entries in event dates
 *
 * TODO:
 *  - rework whole month divider system
 *      - when item is deleted or changed, and no other item needs a month divider for this month, delete month divider
 *  - save events in more map like way, so later adding of properties likes nicknames/favorites are easier to parse
 *
 */
object EventHandler {

    /**
     * event_list a list used for sorted viewing of the maps content
     * the data is stored in pairs of EventDay and the index of this dataset in the map as an int
     */
    private var event_list: List<EventDate> = emptyList<EventDate>()

    private var event_map: MutableMap<Int, EventDate> = emptyMap<Int, EventDate>().toMutableMap()

    /**
     * addEvent adds a EventDay type to the map and has the possibility to write it to the shared prefernces after adding it
     * this orders all events after the date automatically
     * also updates the eventday list after every adding of a new event
     * @param event: EventDay
     * @param context: Context
     * @param writeAfterAdd: Boolean whether this event should be written to sharedpref after adding to list
     * @param newEntry : Boolean, whether a new notification should be created after adding this event
     */
    fun addEvent(
        event: EventDate,
        context: Context,
        writeAfterAdd: Boolean = true,
        addNewNotification: Boolean = true,
        updateEventList: Boolean = true,
        addBitmap: Boolean = true

    ) {
        this.event_map[event.eventID] = event

        if (event is EventBirthday && addBitmap) {
            Thread(Runnable {
                if (event.avatarImageUri != null) {
                    BitmapHandler.addDrawable(
                        event.eventID,
                        Uri.parse(event.avatarImageUri),
                        context,
                        readBitmapFromGallery = false
                    )
                }
                if (context is MainActivity) {
                    context.runOnUiThread {
                        if (context.recyclerView != null) {
                            context.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }).start()
        }

        //set hour of day from all other events except monthdivider to 12h (month divider is at 0h), so when sorting month divider is always at first
        if (event !is MonthDivider && addNewNotification) {
            val cal = Calendar.getInstance()
            cal.time = event.eventDate
            cal.set(Calendar.HOUR_OF_DAY, 12)
            event.eventDate = cal.time
            NotificationHandler.scheduleNotification(context, event)
        }

        if (updateEventList) {
            this.event_list = getSortedListBy()
        }

        if (writeAfterAdd) {
            IOHandler.writeEventToFile(event.eventID, event)
        }
    }

    fun clearData() {
        if (this.event_list.isNotEmpty()) {
            this.event_map.clear()
            this.event_list = getSortedListBy()
        }
    }

    /**
     * getEventByPosition returns the value with type EventDay? to a given integer key
     * @param key : Int
     * @return EventDay?
     */
    fun getEventByPosition(key: Int): EventDate? {
        if (containsIndex(key))
            return event_list[key]
        return null
    }

    /**
     * getEventToEventIndex returns the value with type EventDay? to a given integer key
     * @param index : Int
     * @return EventDay?
     */
    fun getEventToEventIndex(index: Int): EventDate? {
        if (event_map.containsKey(index))
            return event_map[index]
        return null
    }

    /**
     * removeEventByKey removes an event from the by using a key
     *
     * @param index : Int
     */
    fun removeEventByID(index: Int, context: Context, writeChange: Boolean = false) {
        getEventToEventIndex(index)?.let { event ->

            if (event is EventBirthday) {
                BitmapHandler.removeBitmap(index, context)
            }

            NotificationHandler.cancelNotification(context, event)

            if (writeChange) {
                IOHandler.removeEventFromFile(event.eventID)
            }

            event_map.remove(index)
            this.event_list = this.getSortedListBy()
        }
    }

    fun deleteAllEntriesAndImages(context: Context, writeAfterAdd: Boolean) {
        this.event_list.forEach {
            NotificationHandler.cancelNotification(context, it)
        }

        this.clearData()
        BitmapHandler.removeAllDrawables(context)
        if (writeAfterAdd) {
            //deletes shared prefs before writing list, but list is empty, so it only clears the shared prefs
            IOHandler.writeAll()
        }
    }

    /**
     * changeEventAt assign new event at key position
     *
     * @param ID : Int
     * @param newEvent : EventDay
     */
    fun changeEventAt(ID: Int, newEvent: EventDate, context: Context, writeAfterChange: Boolean = false) {
        getEventToEventIndex(ID)?.let { oldEvent ->
            newEvent.eventID = ID
            //set hour of day from all other events except monthdivider to 12h (month divider is at 0h), so when sorting month divider is always at first
            if (newEvent !is MonthDivider) {
                val cal = Calendar.getInstance()
                cal.time = newEvent.eventDate
                cal.set(Calendar.HOUR_OF_DAY, 12)
                newEvent.eventDate = cal.time
            }

            NotificationHandler.cancelNotification(context, oldEvent)
            NotificationHandler.scheduleNotification(context, newEvent)

            this.event_map[ID] = newEvent

            if (newEvent is EventBirthday && newEvent.avatarImageUri != null) {
                //force bitmaphandler to load new avatar image from gallery, in case there is already an existant bitmap
                BitmapHandler.addDrawable(
                    ID,
                    Uri.parse((newEvent).avatarImageUri),
                    context,
                    readBitmapFromGallery = true
                )
            }

            this.event_list = getSortedListBy()

            if (writeAfterChange) {
                IOHandler.writeEventToFile(ID, newEvent)
            }
        }
    }

    /**
     * containsKey checks if the given key is present in the map
     *
     * @param index: Int
     * @return Boolean
     */
    fun containsIndex(index: Int): Boolean {
        return event_map.containsKey(index)
    }

    /**
     * getLastIndex returns the last used index in the map
     * The indexes of the map specify the event value, they are always incremented by one when a new value is added
     * So therefore to get the last used index, its enough to just check the map size
     *
     * It returns 0 if the map is empty
     *
     * @return Int
     */
    fun getLastIndex(): Int {
        return event_map.keys.sorted().last()
    }

    /**
     * generateRandomEventDates does exactly what the name says
     * Only used for testing purposes!
     *
     * @param count : Int
     */
    fun generateRandomEventDates(count: Int, context: Context, writeAfterAdd: Boolean = false) {
        for (i in 1..count) {

            val day: Int = (1..30).random()
            val month: Int = (1..12).random()
            val year: Int = (0..99).random()
            val random = java.util.Random()
            val isYearGiven: Boolean = random.nextBoolean()

            val event = EventBirthday(
                EventDate.parseStringToDate(
                    "$day.$month.$year",
                    DateFormat.SHORT,
                    Locale.GERMAN
                ), EventHandler.getLastIndex().toString(), (i * i).toString(), isYearGiven
            )
            if (isYearGiven) {
                event.note = (day + month + i).toString()
            }
            addEvent(event, context, writeAfterAdd)
        }
    }

    fun getList(): List<EventDate> {
        return this.event_list
    }

    fun getMap(): Map<Int, EventDate> {
        return this.event_map
    }

    /**
     * getSortValueListBy returns the map as a value list which is sorted by specific attributes given by an enum identifier
     * If the identifier is unknown, than an empty value list is returned
     *
     * @param identifier : SortIdentifier
     * @return List<EventDay>
     */
    fun getSortedListBy(
        identifier: SortIdentifier = EventDate.Identifier.Date
    ): List<EventDate> {
        if (identifier == EventDate.Identifier.Date) {
            return event_map.values.sortedWith(
                compareBy(
                    { it.getDayOfYear() },
                    { it.getMonth() },
                    { it.getHourOfDay() })
            )
        } else {
            return emptyList()
        }
    }
}