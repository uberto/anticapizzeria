package com.gamasoft.anticapizzeria.application

import com.gamasoft.anticapizzeria.eventStore.EventStore
import com.gamasoft.anticapizzeria.readModel.Entity
import com.gamasoft.anticapizzeria.readModel.Query
import com.gamasoft.anticapizzeria.readModel.QueryHandler
import com.gamasoft.anticapizzeria.writeModel.Command
import com.gamasoft.anticapizzeria.writeModel.CommandHandler


class Application {

    val eventStore = EventStore()

    val commandHandler = CommandHandler(eventStore)
    val queryHandler = QueryHandler()

    private var started: Boolean = false

    fun start() {
        eventStore.loadAllEvents()
        eventStore.addListener(queryHandler.eventChannel)
        started = true
    }

    fun stop() {
        eventStore.saveAllEvents()

    }

    fun process(c: Command): String {
        if (!started)
            throw NotStartedException()
        return commandHandler.handle(c)
    }

    fun process(q: Query<out Entity>): List<Entity> {
        return queryHandler.handle(q)
    }

}


