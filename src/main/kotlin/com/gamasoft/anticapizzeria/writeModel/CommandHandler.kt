package com.gamasoft.anticapizzeria.writeModel

import arrow.data.Validated
import arrow.data.ValidatedNel
import com.gamasoft.anticapizzeria.application.createActor
import com.gamasoft.anticapizzeria.eventStore.*
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking


typealias ErrorMsg = String
typealias CmdResult = ValidatedNel<ErrorMsg, Event>
typealias EsScope = EventStore.() -> CmdResult

data class CommandMsg(val command: Command, val response: CompletableDeferred<CmdResult>) // a command with a result


class CommandHandler(val eventStore: EventStore) {


    //if we need we can have multiple instances
    val sendChannel = createActor<CommandMsg> { executeCommand(it) }

    private fun executeCommand(msg: CommandMsg) {

        val res = executeMulti(msg.command)(eventStore)

        if (res is Validated.Valid)
            runBlocking { //use launch to store events in parallel slightly out of order
                eventStore.sendChannel.send(res.a)
                delay(10) //simulate network delay
            }
        msg.response.complete(res)
    }

    private fun executeMulti(c: Command): EsScope {

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


    fun handle(cmd: Command): CompletableDeferred<CmdResult> {

        val msg = CommandMsg(cmd, CompletableDeferred())

        runBlocking { //use launch to execute commands in parallel slightly out of order
            sendChannel.send(msg)
        }

        return msg.response
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
        Validated.validNel(ItemCreated(c.itemId, c.desc, c.price))
    else
        Validated.invalidNel("Item already existing! ${item}")
}

private fun execute(c: EditItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item is EnabledItem)
        Validated.validNel(ItemEdited(c.itemId, c.desc, c.price))
    else
        Validated.invalidNel("Item not enabled! ${item}")
}

private fun execute(c: DisableItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item is EnabledItem)
        Validated.validNel(ItemDisabled(c.itemId))
    else
        Validated.invalidNel("Item already disabled! ${item}")
}

private fun execute(c: EnableItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()
    if (item is DisabledItem)
        Validated.validNel(ItemEnabled(c.itemId))
    else
        Validated.invalidNel("Item already enabled! ${item}")
}




private fun execute(c: StartOrder): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    if (order == emptyOrder)
        Validated.validNel(Started(c.phoneNum))
    else
        Validated.invalidNel("Order already existing! ${order}")
}


private fun execute(c: AddItem): EsScope = {
    val item = getEvents<ItemEvent>(c.itemId).fold()

    when (item) {
        is DisabledItem -> Validated.invalidNel("Cannot add disabled item! {$item}")
        else -> {
            val order = getEvents<OrderEvent>(c.phoneNum).fold()
            when (order) {
                is NewOrder -> Validated.validNel(ItemAdded(c.phoneNum, c.itemId, c.quantity))
                is ReadyOrder -> Validated.validNel(ItemAdded(c.phoneNum, c.itemId, c.quantity))
                else ->
                    Validated.invalidNel("Order cannot be modified! ${order}")
            }
        }
    }
}

private fun execute(c: RemoveItem): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is NewOrder -> if (order.details.any { it.itemId ==c.itemId})
                        Validated.validNel(ItemRemoved(c.phoneNum, c.itemId))
                        else
                            Validated.invalidNel("Item not present in the order! ${order}")
        is ReadyOrder -> if (order.details.any { it.itemId ==c.itemId})
                            Validated.validNel(ItemRemoved(c.phoneNum, c.itemId))
                        else
                            Validated.invalidNel("Item not present in the order! ${order}")
    else ->
        Validated.invalidNel("Order cannot be modified! ${order}")
    }
}

private fun execute(c: AddAddress): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is NewOrder -> Validated.validNel(AddressAdded(c.phoneNum, c.address))
    else ->
        Validated.invalidNel("Address cannot be added! ${order}")
    }
}

private fun execute(c: Confirm): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ReadyOrder -> Validated.validNel(Confirmed(c.phoneNum))
    else ->
        Validated.invalidNel("Order is not ready for confirmation! ${order}")
    }
}

private fun execute(c: Cancel): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ReadyOrder -> Validated.validNel(Cancelled(c.phoneNum))
        is NewOrder -> Validated.validNel(Cancelled(c.phoneNum))
    else ->
        Validated.invalidNel("Order cannot be cancelled now! ${order}")
    }
}

private fun execute(c: Pay): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ConfirmedOrder -> Validated.validNel(Paid(c.phoneNum, c.price))
    else ->
        Validated.invalidNel("Order cannot be paid now! ${order}")
    }
}

private fun execute(c: Refuse): EsScope = {
    val order = getEvents<OrderEvent>(c.phoneNum).fold()
    when (order){
        is ConfirmedOrder -> Validated.validNel(Refused(c.phoneNum, c.reason))
    else ->
        Validated.invalidNel("Order cannot be refused now! ${order}")
    }
}



