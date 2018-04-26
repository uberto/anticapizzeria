package com.gamasoft.anticapizzeria.application

import arrow.data.Nel
import arrow.data.Validated
import com.gamasoft.anticapizzeria.eventStore.EventStoreInMemory
import com.gamasoft.anticapizzeria.readModel.ReadEntity
import com.gamasoft.anticapizzeria.readModel.Query
import com.gamasoft.anticapizzeria.readModel.QueryHandler
import com.gamasoft.anticapizzeria.writeModel.CmdResult
import com.gamasoft.anticapizzeria.writeModel.Command
import com.gamasoft.anticapizzeria.writeModel.CommandHandler
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.runBlocking


class Application {

    val eventStore = EventStoreInMemory()

    val commandHandler = CommandHandler(eventStore)
    val queryHandler = QueryHandler()

    fun start() {
        eventStore.addListener(queryHandler.eventChannel)
        eventStore.loadAllEvents()
    }

    fun stop() {
        eventStore.saveAllEvents()

    }

    fun Command.process(): CompletableDeferred<CmdResult> {
        return commandHandler.handle(this)
    }

    fun Query<out ReadEntity>.process(): List<ReadEntity> {
        return queryHandler.handle(this)
    }


    fun List<Command>.processAllInSync(): List<Nel<String>> {

        val completed = runBlocking {
             this@processAllInSync.map { it.process() }
                    .map { it.await() }
        }


        return completed.filter { it.isInvalid }
            .map { (it as Validated.Invalid).e }
    }

}


