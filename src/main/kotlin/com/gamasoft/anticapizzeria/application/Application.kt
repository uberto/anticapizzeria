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
    val queryHandler = QueryHandler(eventStore)

//    fun start() {
//
//    }
//
//    fun stop() {
//
//    }

    fun process(c: Command): String {
        return commandHandler.handle(c)
    }

    fun process(q: Query<out Entity>): List<Entity> {
        return queryHandler.handle(q)
    }

}


