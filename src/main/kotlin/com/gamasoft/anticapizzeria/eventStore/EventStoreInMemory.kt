package com.gamasoft.anticapizzeria.eventStore

import com.gamasoft.anticapizzeria.application.createActor
import kotlinx.coroutines.experimental.channels.SendChannel

class EventStoreInMemory : EventStore {

    private val eventCache = mutableMapOf<String, List<Event>>()
    private val listeners: MutableList<SendChannel<Event>> = mutableListOf()

    override val sendChannel = createActor<Event> { processEvents(it) }

    suspend private fun processEvents(event: Event) {

        eventCache.compute(event.pk){k, el -> (el?: listOf()).plus(event)}

        for (listener in listeners) {
            listener.send(event)
        }
    }

    override fun addListener(listener: SendChannel<Event>){
        listeners.add(listener)
    }

    fun saveAllEvents() {
        //not implemented

    }

    fun loadAllEvents() {
        //not implemented
    }

    override fun getEvents(pk: String): List<Event> = eventCache.getOrDefault(pk, listOf())

}
