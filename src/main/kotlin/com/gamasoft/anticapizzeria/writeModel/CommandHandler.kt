package com.gamasoft.anticapizzeria.writeModel

import com.gamasoft.anticapizzeria.application.createActor
import com.gamasoft.anticapizzeria.eventStore.*
import kotlinx.coroutines.experimental.runBlocking


//TODO EventStore with map->List on commands pk/event pk
//TODO refactor out actor from processing
class CommandHandler(val eventStore: EventStore) {

    val sendChannel = createActor<Command> { processCommand(it) }

    suspend private fun processCommand(c: Command) {
        println("Processing $c")
        val event = when(c){
            is CreateItem -> listOf(ItemCreated(c.itemId, c.desc, c.price))
            is StartOrder -> listOf(OrderStarted(c.phoneNum))
            is AddItem -> listOf(ItemAdded(c.phoneNum, c.item, c.quantity))
            is RemoveItem -> listOf(ItemRemoved(c.phoneNum, c.item))
            is AddAddress -> listOf(AddressAdded(c.phoneNum, c.address))
            is Confirm -> listOf(Confirmed(c.phoneNum))
            is Cancel -> listOf(Cancelled(c.phoneNum))
            is Deliver -> listOf(DeliverStarted(c.phoneNum))
            is Pay -> listOf(Paid(c.phoneNum, c.price))
            is DisableItem -> listOf(ItemDisabled(c.itemId))
            is EditItem -> listOf(ItemEdited(c.itemId, c.desc, c.price))
            is EnableItem -> listOf(ItemEnabled(c.itemId))
            is AddBonusItemToAllOpenOrders -> listOf()
            is IncreasePriceToAllEnabledItems -> listOf()
            CancelAllOpenOrders -> listOf()
            is NoDelivery -> listOf(Refused(c.phoneNum, c.reason))
        }

        eventStore.sendChannel.send(event)
    }


    fun handle(cmd: Command):String {

        //TODO validate cmd

        runBlocking {
            sendChannel.send(cmd)

        }

        return "Ok"
    }


}
