package com.gamasoft.anticapizzeria.writeModel

import com.gamasoft.anticapizzeria.eventStore.Event
import com.gamasoft.anticapizzeria.eventStore.EventStore
import com.gamasoft.anticapizzeria.eventStore.ItemCreated
import com.gamasoft.anticapizzeria.eventStore.OrderStarted
import com.gamasoft.anticapizzeria.application.createActor
import kotlinx.coroutines.experimental.runBlocking


//TODO EventStore with map->List on commands pk/event pk
//TODO refactor out actor from processing
class CommandHandler(val eventStore: EventStore) {

    val sendChannel = createActor<Command> { processCommand(it) }

    suspend private fun processCommand(c: Command) {
        println("Processing $c")
        val events = when(c){
            is CreateItem -> listOf(ItemCreated(c.itemId, c.desc, c.price))
            is StartOrder -> listOf(OrderStarted(c.phoneNum))
            else -> { listOf<Event>()
            }
        }

        eventStore.sendChannel.send(events)
    }


    fun handle(cmd: Command):String {

        //validate cmd

        runBlocking {
            sendChannel.send(cmd)

        }
        return "Ok"
    }


}
