package com.gamasoft.anticapizzeria.writeModel

import com.gamasoft.anticapizzeria.application.createActor
import com.gamasoft.anticapizzeria.eventStore.*
import kotlinx.coroutines.experimental.runBlocking

class CommandHandler(val eventStore: EventStore) {

    val sendChannel = createActor<Command> { processCommand(it) }

    suspend private fun processCommand(c: Command) {
        println("Processing $c")
        val event = when(c){
            is CreateItem -> itemCreated(c)(eventStore)
            is StartOrder -> OrderStarted(c.phoneNum)
            is AddItem -> ItemAdded(c.phoneNum, c.item, c.quantity)
            is RemoveItem -> ItemRemoved(c.phoneNum, c.item)
            is AddAddress -> AddressAdded(c.phoneNum, c.address)
            is Confirm -> Confirmed(c.phoneNum)
            is Cancel -> Cancelled(c.phoneNum)
            is Deliver -> DeliverStarted(c.phoneNum)
            is Pay -> Paid(c.phoneNum, c.price)
            is DisableItem -> ItemDisabled(c.itemId)
            is EditItem -> ItemEdited(c.itemId, c.desc, c.price)
            is EnableItem -> ItemEnabled(c.itemId)
            is NoDelivery -> Refused(c.phoneNum, c.reason)
        }

        eventStore.sendChannel.send(event)
    }


    fun handle(cmd: Command):String {

            runBlocking {
                sendChannel.send(cmd)

            }

            return "Ok"
        }
    }



private fun <Event> List<Event>.foldItem(): Item {

    val emptyItem = DisabledItem("", "", 0.0)

    val oper = {i: Item, e:Event -> when (e) {
        is ItemCreated -> EnabledItem(e.itemId, e.desc, e.price)
        is ItemEdited -> EnabledItem(e.itemId, e.desc, e.price)
        is ItemDisabled -> when (i){
            is EnabledItem -> DisabledItem(e.itemId, i.name, i.price)
            else -> i }
        is ItemEnabled -> when (i){
            is DisabledItem -> EnabledItem(e.itemId, i.name, i.price)
            else -> i }
        else -> i
    }}

    return this.fold(emptyItem, oper)
}


private fun <Event> List<Event>.foldOrder(): Order {

    val emptyOrder = NewOrder("", listOf())


    val oper = {o: Order, e:Event -> when (e) {
        is OrderStarted -> NewOrder(e.phoneNum, listOf())
        is ItemAdded -> when (o){
            is NewOrder -> NewOrder(e.phoneNum, o.details.plus(OrderDetail(e.itemId, e.quantity)))
            is ReadyOrder -> ReadyOrder(e.itemId, o.address, o.details.plus(OrderDetail(e.itemId, e.quantity)))
            else -> o }
        is ConfirmedOrder -> emptyOrder
        is DeliveringOrder -> emptyOrder
        is PaidOrder -> emptyOrder
        is CancelledOrder -> emptyOrder
        is RefusedOrder -> emptyOrder
        else -> o
    }}

    return this.fold(emptyOrder, oper)

}




private fun itemCreated(c: CreateItem): (EventStore) -> Event = {
    ev: EventStore ->
    val item = ev.getEvents(c.itemId).foldItem()
    ItemCreated(c.itemId, c.desc, c.price)
}



