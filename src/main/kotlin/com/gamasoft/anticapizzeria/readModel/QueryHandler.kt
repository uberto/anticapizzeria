package com.gamasoft.anticapizzeria.readModel

import com.gamasoft.anticapizzeria.application.createActor
import com.gamasoft.anticapizzeria.eventStore.*
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.runBlocking

data class QueryMsg(val query: Query<out Entity>, val response: CompletableDeferred<List<Entity>>) // a request with reply

class QueryHandler {

    val queryChannel = createActor { qm: QueryMsg -> qm.response.complete(processQuery(qm.query)) }

    val eventChannel = createActor { e: Event -> processEvent(e) }

    private val items = mutableMapOf<String, Item>()
    private val orders = mutableMapOf<String, Order>()

    private fun processEvent(e: Event): Entity? {
        return when (e){
            is OrderStarted -> orders.put(e.phoneNum, Order(OrderStatus.new, e.phoneNum, 0.0, null, mutableListOf()))
            is ItemAdded  -> {
                val item = items.get(e.itemId)
                val order = orders.get(e.phoneNum)
                order?.apply { item?.apply { details.add(OrderDetail(item.name, e.quantity)); total += item.price * e.quantity } }
            }
            is ItemRemoved -> {
                val item = items.get(e.itemId)
                val order = orders.get(e.phoneNum)
                order?.apply { item?.apply {
                    val toRemove = details.filter { it.itemName == item.name }
                    val newTotal = toRemove.fold(total) {a, od -> a + od.qty * item.price}
                    details.removeAll (toRemove)
                    total -= newTotal
                } }
            }
            is AddressAdded -> {
                    val order = orders.get(e.phoneNum)
                    order?.apply { address = e.address; status = OrderStatus.ready} }
            is Confirmed  -> {
                val order = orders.get(e.phoneNum)
                order?.apply { status = OrderStatus.confirmed}
            }
            is Cancelled  ->  {
                val order = orders.get(e.phoneNum)
                order?.apply { status = OrderStatus.cancelled}
            }
            is DeliverStarted ->  {
                val order = orders.get(e.phoneNum)
                order?.apply { status = OrderStatus.leftForDelivery}
            }
            is Paid -> {
                val order = orders.get(e.phoneNum)
                order?.apply { status = OrderStatus.paid; total = e.price}
            }
            is Refused -> {
                val order = orders.get(e.phoneNum)
                order?.apply { status = OrderStatus.refused; total = 0.0}
            }

            is ItemCreated ->  {
                items.put(e.itemId, Item(e.desc, e.price, true))
            }
            is ItemDisabled -> {
                val item = items.get(e.itemId)
                item?.apply { enabled = false }
            }
            is ItemEdited ->{
                val item = items.get(e.itemId)
                item?.apply { name = e.desc; price = e.price }
            }
            is ItemEnabled -> {
                val item = items.get(e.itemId)
                item?.apply { enabled = true }
            }

        }
    }





    private fun processQuery(q: Query<out Entity>): List<Entity> {
        println("Processing $q")

        return when(q){
            GetAllOpenOrders -> orders.values.filter { it.status in setOf(OrderStatus.new, OrderStatus.ready) }
            is GetOrder -> orders.get(q.phoneNum)?.run { listOf(this)}?: listOf()
            GetBiggestOrder -> orders.maxBy { it.value.total }?.value?.run { listOf(this) }?:listOf()
            is GetItem -> items.get(q.itemId)?.run{ listOf(this)}?: listOf()
            GetAllActiveItems -> items.values.filter { it.enabled }
        }
    }




    fun handle(q: Query<out Entity>):List<Entity> {


        val msg = QueryMsg(q, CompletableDeferred())

        runBlocking {
            queryChannel.send(msg)

            msg.response.await()

        }
        return msg.response.getCompleted()

    }

}

