package com.gamasoft.anticapizzeria.eventStore

import com.gamasoft.anticapizzeria.application.createActor

class EventStore {

    private val map = mutableMapOf<String, Event>()
    val sendChannel = createActor<List<Event>> { processEvents(it) }

    private fun processEvents(events: List<Event>) {
        events.map{e -> e.pk() to e}
    }


}
