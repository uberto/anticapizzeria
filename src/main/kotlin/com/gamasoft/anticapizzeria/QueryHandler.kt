package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

data class QueryMsg(val query: Query<out Entity>, val response: CompletableDeferred<List<Entity>>) // a request with reply

class QueryHandler {

    val sendChannel = createActor()


    private fun createActor(): SendChannel<QueryMsg> {
        return actor {

            for (qm in channel) {
                val complete = qm.response.complete(processQuery(qm.query))

                delay(100)
            }
        }
    }

//    private inline fun <reified T: Entity> logQuery(q: Query<T>) {
//        when(T::class){
//            Order::class -> listOf(Order("pizza margherita", 2) as T)
//            Item::class -> listOf(Item("pizza margherita", 123.2) as T)
//        }
//    }

    private fun processQuery(q: Query<out Entity>): List<Entity> {
        println("Processing $q")

        return when(q){
            is GetAllOpenOrders -> listOf(Order("pizza margherita", 2), Order("pizza capricciosa", 1) )
            is OrderQuery -> listOf(Order("pizza margherita", 2) )
            is ItemQuery -> listOf(Item("pizza margherita", 123.2) )
        }
    }


    fun handle(q: Query<out Entity>):List<Entity> {


        val msg = QueryMsg(q, CompletableDeferred())

        runBlocking {
            sendChannel.send(msg)

            msg.response.await()

        }
        return msg.response.getCompleted()

    }

}

