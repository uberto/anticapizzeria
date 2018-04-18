package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.selects.SelectClause1


//TODO EventStore with map->List on commands pk/event pk
//TODO refactor out actor from processing
class CommandHandler {

    val sendChannel: SendChannel<Command>

    init{
        //actor can be multiple instance but should be sticky with command pk
        sendChannel = actor{

            for (c in channel) {
                println("Processing $c")
                delay(1000)
            }

            println("done!")
        }
    }

    fun handle(c: Command):String {

        runBlocking {
            sendChannel.send(c)
            println("sent!")

        }
        return "Ok"
    }


}
