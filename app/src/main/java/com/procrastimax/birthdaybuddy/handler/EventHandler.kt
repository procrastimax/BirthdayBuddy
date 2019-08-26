package com.procrastimax.birthdaybuddy.handler

import android.content.Context
import android.net.Uri
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.MonthDivider
import com.procrastimax.birthdaybuddy.models.SortIdentifier
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

/**
 * EventHandler singleton object map to store all occurring EventDates (birthdays, anniversaries, etc.)
 * This is useful to compare all objects more easily, f.e. when you want to traverse all entries in event dates
 */
object EventHandler {

    /**
     * event_list a list used for sorted viewing of the maps content
     * the data is stored in pairs of EventDay and the index of this dataset in the map as an int
     */
    private var event_list: List<EventDate> = emptyList()

    private var event_map: MutableMap<Int, EventDate> = emptyMap<Int, EventDate>().toMutableMap()

    /**
     * addEvent adds a EventDay type to the map and has the possibility to write it to the shared preferences after adding it
     * this orders all events after the date automatically
     * also updates the Eventday list after every adding of a new event
     * @param event: EventDay
     * @param context: Context
     * @param writeAfterAdd: Boolean whether this event should be written to shared preferences after adding to list
     * @param addNewNotification : Boolean, whether a new notification should be created after adding this event
     * @param updateEventList : Boolean, whether to update the EventList, updating the EventList means sorting event values by their date
     * @param addBitmap : Boolean whether a new bitmap should be added
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
                        readBitmapFromGallery = false,
                        //150dp because the app_bar height is 300dp
                        scale = MainActivity.convertDpToPx(context, 150f)
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

        //set hour of day from all other events except MonthDivider to 12h (month divider is at 0h), so when sorting month divider is always at first
        if (event !is MonthDivider && addNewNotification) {
            NotificationHandler.scheduleNotification(context, event)
        }

        if (updateEventList) {
            this.event_list = getSortedListBy()
        }

        if (writeAfterAdd) {
            IOHandler.writeEventToFile(event.eventID, event)
        }
    }

    /**
     * changeEventAt changes event at key position
     *
     * @param ID : Int
     * @param newEvent : EventDay
     */
    fun changeEventAt(
        ID: Int,
        newEvent: EventDate,
        context: Context,
        writeAfterChange: Boolean = false
    ) {
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
                //remove old drawable if one exists
                if ((oldEvent as EventBirthday).avatarImageUri != null) {
                    BitmapHandler.removeBitmap(oldEvent.eventID, context)
                }
                //force BitmapHandler to load new avatar image from gallery, in case there is already an existant bitmap
                BitmapHandler.addDrawable(
                    ID,
                    Uri.parse((newEvent).avatarImageUri),
                    context,
                    readBitmapFromGallery = true,
                    scale = MainActivity.convertDpToPx(context, 150f)
                )
            }

            this.event_list = getSortedListBy()

            if (writeAfterChange) {
                IOHandler.writeEventToFile(ID, newEvent)
            }
        }
    }

    /**
     * removeEventByKey removes an event from the by using a key
     *
     * @param index : Int
     * @param context : Context
     * @param writeChange : Boolean
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

            this.event_map.remove(index)
            this.event_list = this.getSortedListBy()
        }
    }

    fun clearData() {
        if (this.event_list.isNotEmpty()) {
            this.event_map.clear()
            this.event_list = getSortedListBy()
        }
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

    fun deleteAllEntriesAndImages(context: Context, writeAfterAdd: Boolean) {
        this.event_list.forEach {
            NotificationHandler.cancelNotification(context, it)
        }
        this.clearData()
        BitmapHandler.removeAllDrawables(context)
        if (writeAfterAdd) {
            IOHandler.clearSharedPrefEventData()
        }
    }

    /**
     * containsKey checks if the given key is present in the map
     *
     * @param index: Int
     * @return Boolean
     */
    private fun containsIndex(index: Int): Boolean {
        return event_map.containsKey(index)
    }

    fun getList(): List<EventDate> {
        return this.event_list
    }

    fun getEventsAsStringList(): String {
        var eventString = ""
        val tempList = event_list.toMutableList()
        tempList.forEach {
            //don't save Monthdividers bc they are created with the first start of the app
            if (it !is MonthDivider) {
                //removing avatar image
                if (it is EventBirthday) {
                    eventString += it.toStringWithoutImage() + "\n"
                } else {
                    eventString += it.toString() + "\n"
                }
            }
        }
        return eventString
    }

    /**
     * getSortValueListBy returns the map as a value list which is sorted by specific attributes given by an enum identifier
     * If the identifier is unknown, than an empty value list is returned
     *
     * @param identifier : SortIdentifier
     * @return List<EventDay>
     */
    private fun getSortedListBy(
        identifier: SortIdentifier = EventDate.Identifier.Date
    ): List<EventDate> {
        return if (identifier == EventDate.Identifier.Date) {
            event_map.values.sortedWith(
                compareBy(
                    { it.getDayOfYear() },
                    { it.getDayOfMonth() },
                    { it.getYear() },
                    { it.eventID })
            )
        } else {
            emptyList()
        }
    }
}