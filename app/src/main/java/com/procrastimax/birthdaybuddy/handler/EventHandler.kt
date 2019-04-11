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
    private var event_list: MutableList<EventDate> = emptyList<EventDate>().toMutableList()

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
        this.event_list.add(event)

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
                    (context as MainActivity).runOnUiThread {
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
            this.event_list = getSortedListBy(this.event_list).toMutableList()
        }

        if (writeAfterAdd) {
            IOHandler.writeEventToFile(event.eventID, event)
        }
    }

    fun addList(list: List<EventDate>) {
        this.event_list = getSortedListBy(list).toMutableList()
    }

    fun clearList() {
        if (this.event_list.isNotEmpty()) {
            this.event_list.clear()
        }
    }

    /**
     * getValueToKey returns the value with type EventDay? to a given integer key
     *
     * @param key : Int
     * @return EventDay?
     */
    fun getEvent(key: Int): EventDate? {
        if (containsIndex(key))
            return event_list[key]
        return null
    }

    /**
     * removeEventByKey removes an event from the by using a key
     *
     * @param key : Int
     */
    fun removeEventByKey(key: Int, context: Context, writeChange: Boolean = false) {
        if (event_list[key] is EventBirthday) {
            if ((event_list[key] as EventBirthday).avatarImageUri != null) {
                BitmapHandler.removeBitmap(key, context)
            }
        }

        NotificationHandler.cancelNotification(context, event_list[key])

        if (writeChange) {
            IOHandler.removeEventFromFile(event_list[key].eventID)
        }

        event_list.removeAt(key)
        this.event_list = this.getSortedListBy(this.event_list).toMutableList()
    }

    fun deleteAllEntries(context: Context, writeAfterAdd: Boolean) {
        this.event_list.forEach {
            NotificationHandler.cancelNotification(context, it)
        }

        this.event_list.clear()
        BitmapHandler.removeAllDrawables(context)
        if (writeAfterAdd) {
            //deletes shared prefs before writing list, but list is empty, so it only clears the shared prefs
            IOHandler.writeAll()
        }
    }

    /**
     * changeEventAt assign new event at key position
     *
     * @param key : Int
     * @param event : EventDay
     */
    fun changeEventAt(key: Int, event: EventDate, context: Context, writeAfterChange: Boolean = false) {

        //set hour of day from all other events except monthdivider to 12h (month divider is at 0h), so when sorting month divider is always at first
        if (event !is MonthDivider) {
            val cal = Calendar.getInstance()
            cal.time = event.eventDate
            cal.set(Calendar.HOUR_OF_DAY, 12)
            event.eventDate = cal.time
        }

        NotificationHandler.cancelNotification(context, event_list[key])
        NotificationHandler.scheduleNotification(context, event)

        event.eventID = event_list[key].eventID

        event_list[key] = event

        if (event is EventBirthday) {
            if ((event).avatarImageUri != null) {
                //force bitmaphandler to load new avatar image from gallery, in case there is already an existant bitmap
                BitmapHandler.addDrawable(
                    event_list[key].eventID,
                    Uri.parse((event).avatarImageUri),
                    context,
                    readBitmapFromGallery = true
                )
            }
        }

        this.event_list = getSortedListBy(this.event_list).toMutableList()

        if (writeAfterChange) {
            IOHandler.writeEventToFile(event.eventID, event)
        }
    }

    /**
     * containsKey checks if the given key is present in the map
     *
     * @param key: Int
     * @return Boolean
     */
    fun containsIndex(key: Int): Boolean {
        return (event_list.getOrNull(key) != null)

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
        return event_list.size
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

    /**
     * getSortValueListBy returns the map as a value list which is sorted by specific attributes given by an enum identifier
     * If the identifier is unknown, than an empty value list is returned
     *
     * @param identifier : SortIdentifier
     * @return List<EventDay>
     */
    fun getSortedListBy(
        list: List<EventDate>,
        identifier: SortIdentifier = EventDate.Identifier.Date
    ): List<EventDate> {
        if (identifier == EventDate.Identifier.Date) {
            return list.sortedWith(
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