package com.gamasoft.anticapizzeria.eventStore

import com.gamasoft.anticapizzeria.application.createActor
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

class EventStoreInMemory : EventStore() {
    override fun getItemEvents(pk: String) = itemEventCache.getOrDefault(pk, emptyList())

    override fun getOrderEvents(pk: String) = orderEventCache.getOrDefault(pk, emptyList())

    private val orderEventCache = mutableMapOf<String, List<OrderEvent>>()
    private val itemEventCache = mutableMapOf<String, List<ItemEvent>>()
    private val listeners: MutableList<SendChannel<Event>> = mutableListOf()

    override val sendChannel = createActor<Event> { processEvents(it) }

    private fun processEvents(event: Event) {

        when (event) {
            is ItemEvent -> itemEventCache.compute(event.key()) { _, el -> (el ?: emptyList()).plus(event) }
            is OrderEvent -> orderEventCache.compute(event.key()) { _, el -> (el ?: emptyList()).plus(event) }
        }

        for (listener in listeners) {
            launch { listener.send(event) }
        }

        println("Processed Event $event")
    }

    override fun addListener(listener: SendChannel<Event>) {
        listeners.add(listener)
    }

    fun saveAllEvents() {
        //persist all events
        //not implemented

    }

    fun loadAllEvents() {
        //load all events from persistence
        //not implemented
    }

}


