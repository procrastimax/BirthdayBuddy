package com.procrastimax.birthdaybuddy.models

import android.util.Log

/**
 * EventHandler singleton object map to store all occurring eventdates (birthdays, anniversaries, etc.)
 * This is useful to compare all objects more easily, f.e. when you want to traverse all entries in event dates
 *
 * THIS IS NOT AN ACTUAL EVENTHANDLER KNOWN FROM EVENT BASED PROGRAMMING
 */
object EventHandler {
    private var events: MutableMap<Int, EventDay> = emptyMap<Int, EventDay>().toMutableMap()

    /**
     * addEvent adds a EventDay type to the map
     * @param event: EventDay
     */
    fun addEvent(event: EventDay) {
        //TODO: add event valdiation
        if (!events.containsValue(event)) {
            val last_key = events.size
            events[last_key] = event
        } else {
            Log.d("EventHandler", "Event already in map when trying to add it")
            val last_key = events.size
            events[last_key] = event
        }
    }

    /**
     * getKeyToValue searches for the key to an given event
     * This should work, because every event should be unique
     *
     * @param event : Eventday
     * @return Int
     */
    fun getKeyToValue(event: EventDay): Int {
        //TODO: this can be really slow
        val entrySet = events.entries
        entrySet.asIterable().forEach {
            if (it.value == event) {
                return it.key
            }
        }
        return -1
    }

    /**
     * getValueToKey returns the value with type EventDay? to a given integer key
     *
     * @param key : Int
     * @return EventDay?
     */
    fun getValueToKey(key: Int): EventDay? {
        if (this.events.contains(key)) {
            return this.events[key]!!
        }
        return null
    }

    /**
     * removeEventByEvent removes an event from the map by using a value
     * It uses the getKeyToValue function
     *
     * @param event : EventDay
     */
    fun removeEventByEvent(event: EventDay) {
        val entrySet = events.entries
        events.remove(getKeyToValue(event))
    }

    /**
     * removeEventByKey removes an event from the by using a key
     *
     * @param key : Int
     */
    fun removeEventByKey(key: Int) {
        events.remove(key)
    }

    /**
     * containsValue checks if the given event is present in the map
     *
     * @param event: EventDay
     * @return Boolean
     */
    fun containsValue(event: EventDay): Boolean {
        return this.events.containsValue(event)
    }

    /**
     * containsKey checks if the given key is present in the map
     *
     * @param key: Int
     * @return Boolean
     */
    fun containsKey(key: Int): Boolean {
        return this.events.contains(key)
    }

    /**
     * getEvents returns all events as Map<Int, EventDay>
     *
     * @return Map<Int, EventDay>
     */
    fun getEvents(): Map<Int, EventDay> {
        return this.events
    }

    /**
     * getLastIndex returns the last used index in the map
     * The indexes of the map specify the event value, they are always incremented by one when a new value is added
     * So therefore to get the last used index, its enough to just check the map size
     *
     * It returns -1 if the map is empty
     *
     * @return Int
     */
    fun getLastIndex(): Int {
        return if (this.events.isEmpty()) {
            -1
        } else this.events.size - 1
    }
}