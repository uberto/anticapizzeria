package com.gamasoft.anticapizzeria.application

import com.gamasoft.anticapizzeria.eventStore.Event
import com.gamasoft.anticapizzeria.eventStore.EventStoreInMemory
import com.gamasoft.anticapizzeria.readModel.Entity
import com.gamasoft.anticapizzeria.readModel.Query
import com.gamasoft.anticapizzeria.readModel.QueryHandler
import com.gamasoft.anticapizzeria.writeModel.Command
import com.gamasoft.anticapizzeria.writeModel.CommandHandler


class Application {

    val eventStore = EventStoreInMemory()

    val commandHandler = CommandHandler(eventStore)
    val queryHandler = QueryHandler()

    private var started: Boolean = false

    fun start() {
        eventStore.addListener(queryHandler.eventChannel)
        eventStore.loadAllEvents()
    }

    fun stop() {
        eventStore.saveAllEvents()

    }

    fun process(c: Command): String {
        return commandHandler.handle(c)
    }

    fun process(q: Query<out Entity>): List<Entity> {
        return queryHandler.handle(q)
    }


    fun processAll(commands: List<Command>):String {
        for (c in commands) {
            val r = process(c)
            if (r != "Ok")
                return r;
        }
        return "Ok"
    }

}


