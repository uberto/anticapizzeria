package com.gamasoft.anticapizzeria.eventStore

import kotlinx.coroutines.experimental.channels.SendChannel

interface EventStore {
    val sendChannel: SendChannel<Event>

    fun addListener(listener: SendChannel<Event>)
    fun getEvents(pk: String): List<Event>
}