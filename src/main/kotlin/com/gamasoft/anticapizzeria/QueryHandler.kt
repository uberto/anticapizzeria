package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

data class QueryMsg<T>(val query: Query<T>, val response: CompletableDeferred<List<T>>) // a request with reply

class QueryHandler {

    val sendChannel = createActor()


    private fun createActor(): SendChannel<QueryMsg<*>> {
        return actor {

            for (q in channel) {
                println("Processing $q")
                delay(10)
            }
        }
    }

    inline fun <reified T> handle(q: Query<T>):List<T> {

        val result:List<T> = mutableListOf()
        val msg = QueryMsg(q, CompletableDeferred(result))

        runBlocking {
            sendChannel.send(msg)

            msg.response.await()
        }

        return result
    }

}

