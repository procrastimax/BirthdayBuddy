package com.procrastimax.birthdaybuddy.handler

import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.OneTimeEvent

/**
 * SearchHandler is a singleton class to retrieve event item indexes from specific search terms
 */
object SearchHandler {

    fun searchOnEventData(searchString: String): List<Int> {
        return searchOnEventData(searchString, EventHandler.getList())
    }

    fun searchOnEventData(searchString: String, events: List<EventDate>): List<Int> {
        val indexList = emptyList<Int>().toMutableList()
        val processedWords = getAllProcessedWords(events)

        processedWords.forEach { pair ->
            pair.second.forEach {
                if (it.startsWith(searchString, ignoreCase = true)) {
                    indexList.add(pair.first)
                }
            }
        }
        return indexList
    }

    private fun getAllProcessedWords(data: List<EventDate>): List<Pair<Int, List<String>>> {
        val wordList = emptyList<Pair<Int, List<String>>>().toMutableList()
        data.forEach {
            processData(it)?.let { pair -> wordList.add(pair) }
        }
        return wordList
    }

    /**
     * processData converts an EventDate dataset to multiple strings which are lowercase and without white spaces
     * @param eventData : EventDate
     * @return Pair<Int, List<String>>?
     */
    private fun processData(eventData: EventDate): Pair<Int, List<String>>? {
        when (eventData) {
            //has forename, surname, nickname
            is EventBirthday -> {
                //all names which are separated by whitespaces/ minus char should be handled as independent words
                val forenames = splitStringToList(eventData.forename)
                val surnames = splitStringToList(eventData.surname)
                val nicknames = splitStringToList(eventData.nickname)

                val nameList = emptyList<String>().toMutableList().apply {
                    if (forenames != null) {
                        addAll(forenames)
                    }
                    if (surnames != null) {
                        addAll(surnames)
                    }
                    if (nicknames != null) {
                        addAll(nicknames)
                    }
                }
                return Pair(eventData.eventID, processStringList(nameList))
            }

            //only has a description name
            is AnnualEvent -> {
                return Pair(
                    eventData.eventID,
                    processStringList(splitStringToList(eventData.name)!!.toMutableList())
                )
            }
            //only has a description name
            is OneTimeEvent -> {
                return Pair(
                    eventData.eventID,
                    processStringList(splitStringToList(eventData.name)!!.toMutableList())
                )
            }
        }
        //don't add month dividers
        return null
    }

    private fun processStringList(dataString: MutableList<String>): List<String> {
        for (i in 0 until dataString.size) {
            dataString[i] = dataString[i].toLowerCase()
        }
        return dataString
    }

    fun splitStringToList(dataString: String?): List<String>? {
        //split all string into lists
        //split string at whitespace char or "-"
        //only return a list containing NOTEMPTY elements
        return dataString?.split("[\\s-]".toRegex())?.filterNot { s -> s.isEmpty() }
    }
}