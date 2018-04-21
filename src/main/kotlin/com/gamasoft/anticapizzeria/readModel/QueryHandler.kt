package com.gamasoft.anticapizzeria.readModel

import com.gamasoft.anticapizzeria.application.createActor
import com.gamasoft.anticapizzeria.eventStore.EventStore
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.runBlocking

data class QueryMsg(val query: Query<out Entity>, val response: CompletableDeferred<List<Entity>>) // a request with reply

class QueryHandler(val eventStore: EventStore) {

    val sendChannel = createActor { qm: QueryMsg -> qm.response.complete(processQuery(qm.query)) }


    private fun processQuery(q: Query<out Entity>): List<Entity> {
        println("Processing $q")

        return when(q){
            GetAllOpenOrders -> listOf(Order(OrderStatus.open, "123", 12.0, arrayOf(OrderDetails("margherita", 2))),
                    Order(OrderStatus.open, "124", 15.0, arrayOf(OrderDetails("capricciosa", 2))))
            is GetOrderStatus -> listOf(Order(OrderStatus.open, q.phoneNum, 0.0, arrayOf()))
            GetBiggestOrder -> listOf(Order(OrderStatus.open, "235", 100.0, arrayOf()))
            is GetItem -> listOf(Item("margherita", 6.0))
            GetAllActiveItems -> listOf(Item("margherita", 6.0), Item("capricciosa", 7.5))
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

