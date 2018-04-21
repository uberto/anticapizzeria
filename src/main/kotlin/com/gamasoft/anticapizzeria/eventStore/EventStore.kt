package com.gamasoft.anticapizzeria.eventStore

import com.gamasoft.anticapizzeria.application.createActor
import kotlinx.coroutines.experimental.channels.SendChannel

class EventStore {

    private val eventCache = mutableMapOf<String, List<Event>>()
    private val listeners: MutableList<SendChannel<Event>> = mutableListOf()

    val sendChannel = createActor<List<Event>> { processEvents(it) }

    suspend private fun processEvents(events: List<Event>) {
        events.map{e -> e.pk() to eventCache.getOrDefault(e.pk(), listOf()).plus(e)}
               .forEach {eventCache.set(it.first, it.second)}

        for (event in events) {

            for (listener in listeners) {
                listener.send(event)
            }
        }
    }

    fun addListener(listener: SendChannel<Event>){
        listeners.add(listener)
    }

    fun saveAllEvents() {
        //not implemented

    }

    fun loadAllEvents() {
        //not implemented
    }

}
