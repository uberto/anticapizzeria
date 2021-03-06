package com.gamasoft.anticapizzeria.application

import com.gamasoft.anticapizzeria.eventStore.EventStoreInMemory
import com.gamasoft.anticapizzeria.functional.Invalid
import com.gamasoft.anticapizzeria.readModel.ReadEntity
import com.gamasoft.anticapizzeria.readModel.Query
import com.gamasoft.anticapizzeria.readModel.QueryHandler
import com.gamasoft.anticapizzeria.writeModel.CmdResult
import com.gamasoft.anticapizzeria.writeModel.Command
import com.gamasoft.anticapizzeria.writeModel.CommandHandler
import com.gamasoft.anticapizzeria.writeModel.DomainError
import kotlinx.coroutines.experimental.*
import java.lang.Thread.sleep


class Application {

    private val eventStore = EventStoreInMemory()
    private val commandHandler = CommandHandler(eventStore)
    private val queryHandler = QueryHandler()

    fun start() {
        eventStore.addListener(queryHandler.eventChannel)
        eventStore.loadAllEvents()
    }

    fun stop() {
        eventStore.saveAllEvents()
    }

    fun List<Command>.processAllInSync(): List<DomainError> =
        runBlocking {
            map {
                it.process().await()
                yield()
            }
            .filterIsInstance<Invalid<DomainError>>()
            .map { it.err }
        }

    fun List<Command>.processAllAsync(): Deferred<List<CmdResult>> =
        async {
            map { it.process() }.map { it.await() }
        }

    fun Command.process(): CompletableDeferred<CmdResult> {
        return commandHandler.handle(this)
    }

    fun Query.process(): List<ReadEntity> {
        return queryHandler.handle(this)
    }

}


