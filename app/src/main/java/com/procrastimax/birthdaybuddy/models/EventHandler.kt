package com.procrastimax.birthdaybuddy.models

import android.util.Log

/**
 * EventHandler singleton object map to store all occurring eventdates (birthdays, anniversaries, etc.)
 * This is useful to compare all objects more easily, f.e. when you want to traverse all entries in event dates
 * THIS IS NOT AN ACTUAL EVENTHANDLER KNOWN FROM EVENT BASED PROGRAMMING
 */
object EventHandler {
    private var events: MutableMap<Int, EventDay> = emptyMap<Int, EventDay>().toMutableMap()

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

    fun getValueToKey(key: Int): EventDay? {
        if (this.events.contains(key)) {
            return this.events[key]!!
        }
        return null
    }

    fun removeEvent(event: EventDay) {
        val entrySet = events.entries
        events.remove(getKeyToValue(event))
    }

    fun removeEvent(key: Int) {
        events.remove(key)
    }

    fun containsValue(event: EventDay): Boolean {
        return this.events.containsValue(event)
    }

    fun containsKey(key: Int): Boolean {
        return this.events.contains(key)
    }

    fun getEvents(): Map<Int, EventDay> {
        return this.events
    }

    fun getLastIndex(): Int {
        return if (this.events.isEmpty()) {
            -1
        } else this.events.size - 1
    }
}