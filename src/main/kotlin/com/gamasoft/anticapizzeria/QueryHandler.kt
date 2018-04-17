package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

class QueryHandler {

    val sendChannel: SendChannel<Query>
    init{
        sendChannel = actor{
            for (q in channel) {
                println("Processing $q")
                delay(10)
            }
        }
    }

    fun handle(q: Query):String {

        runBlocking {
            sendChannel.send(q)

        }
        return "Ok"
    }

}
