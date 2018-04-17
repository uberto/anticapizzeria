package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.runBlocking

class QueryHandler {

    val sendChannel: SendChannel<Query>
    init{
        sendChannel = actor{

        }
    }

    fun handle(q: Query):String {

        runBlocking {
            sendChannel.send(q)

        }
        return "Ok"
    }

}
