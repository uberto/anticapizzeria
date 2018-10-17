package com.gamasoft.anticapizzeria.writeModel

import com.gamasoft.anticapizzeria.application.createActor
import com.gamasoft.anticapizzeria.eventStore.*
import com.gamasoft.anticapizzeria.functional.Invalid
import com.gamasoft.anticapizzeria.functional.Valid
import com.gamasoft.anticapizzeria.functional.Validated
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.runBlocking


typealias CmdResult = Validated<DomainError, Event>
typealias EsScope = EventStore.() -> CmdResult

data class CommandMsg(val command: Command, val response: CompletableDeferred<CmdResult>) // a command with a result


class CommandHandler(val eventStore: EventStore) {

    //if we need we can have multiple instances
    val sendChannel = createActor<CommandMsg> { executeCommand(it) }

    private fun executeCommand(msg: CommandMsg) {

        val res = processPoly(msg.command)(eventStore)

        runBlocking {
            //we want to reply after sending the event to the store
            if (res is Valid) {
                eventStore.sendChannel.send(res.value)
            }
            msg.response.complete(res)
        }
    }

    private fun processPoly(c: Command): EsScope {

        println("Processing ${c}")

        val cmdResult = when (c) {
            is StartOrder -> execute(c)
            is AddItem -> execute(c)
            is RemoveItem -> execute(c)
            is AddAddress -> execute(c)
            is Confirm -> execute(c)
            is Cancel -> execute(c)
            is Pay -> execute(c)
            is Refuse -> execute(c)
            is CreateItem -> execute(c)
            is EditItem -> execute(c)
            is DisableItem -> execute(c)
            is EnableItem -> execute(c)
        }
        return cmdResult
    }


    fun handle(cmd: Command): CompletableDeferred<CmdResult> =
            runBlocking {
                //use launch to execute commands in parallel slightly out of order
                CommandMsg(cmd, CompletableDeferred()).let {
                    sendChannel.send(it)
                    it.response
                }
            }

}


private fun List<ItemEvent>.fold(): Item {
    return this.fold(emptyItem) { i: Item, e: ItemEvent -> i.compose(e)}
}

private fun List<OrderEvent>.fold(): Order {
    return this.fold(emptyOrder) { o: Order, e: OrderEvent -> o.compose(e)}
}

private fun execute(c: CreateItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item == emptyItem)
        Valid(ItemCreated(c.itemId, c.desc, c.price))
    else
        Invalid(ItemError("Item already existing! ${item}", item))
}

private fun execute(c: EditItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item is EnabledItem)
        Valid(ItemEdited(c.itemId, c.desc, c.price))
    else
        Invalid(ItemError("Item not enabled! ${item}", item))
}

private fun execute(c: DisableItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item is EnabledItem)
        Valid(ItemDisabled(c.itemId))
    else
        Invalid(ItemError("Item already disabled! ${item}", item))
}

private fun execute(c: EnableItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item is DisabledItem)
        Valid(ItemEnabled(c.itemId))
    else
        Invalid(ItemError("Item already enabled! ${item}", item))
}




private fun execute(c: StartOrder): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    if (order == emptyOrder)
        Valid(Started(c.phoneNum))
    else
        Invalid(OrderError("Order already existing! ${order}", order))
}


private fun execute(c: AddItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()

    when (item) {
        is DisabledItem -> Invalid(ItemError("Cannot add disabled item! $item", item))
        is emptyItem -> Invalid(ItemError("Cannot add non existing item! ${c.itemId}", item))
        is EnabledItem -> {
            val order = getEvents<OrderEvent>(c.phoneNum).fold()
            when (order) {
                is NewOrder -> Valid(ItemAdded(c.phoneNum, c.itemId, c.quantity))
                is ReadyOrder -> Valid(ItemAdded(c.phoneNum, c.itemId, c.quantity))
                else ->
                    Invalid(OrderError("Order cannot be modified! ${order}", order))
            }
        }
    }
}

private fun execute(c: RemoveItem): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is NewOrder -> if (order.details.any { it.itemId ==c.itemId})
                        Valid(ItemRemoved(c.phoneNum, c.itemId))
                        else
                            Invalid(OrderError("Item ${c.itemId} not present in the order! ${order}", order))
        is ReadyOrder -> if (order.details.any { it.itemId ==c.itemId})
                            Valid(ItemRemoved(c.phoneNum, c.itemId))
                        else
                            Invalid(OrderError("Item ${c.itemId} not present in the order! ${order}", order))
    else ->
        Invalid(OrderError("Order cannot be modified! ${order}", order))
    }
}

private fun execute(c: AddAddress): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is NewOrder -> Valid(AddressAdded(c.phoneNum, c.address))
    else ->
        Invalid(OrderError("Address cannot be added! ${order}", order))
    }
}

private fun execute(c: Confirm): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ReadyOrder -> Valid(Confirmed(c.phoneNum))
    else ->
        Invalid(OrderError("Order is not ready for confirmation! ${order}", order))
    }
}

private fun execute(c: Cancel): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ReadyOrder -> Valid(Cancelled(c.phoneNum))
        is NewOrder -> Valid(Cancelled(c.phoneNum))
    else ->
        Invalid(OrderError("Order cannot be cancelled now! ${order}", order))
    }
}

private fun execute(c: Pay): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ConfirmedOrder -> Valid(Paid(c.phoneNum, c.price))
    else ->
        Invalid(OrderError("Order cannot be paid now! ${order}", order))
    }
}

private fun execute(c: Refuse): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ConfirmedOrder -> Valid(Refused(c.phoneNum, c.reason))
    else ->
        Invalid(OrderError("Order cannot be refused now! ${order}", order))
    }
}



