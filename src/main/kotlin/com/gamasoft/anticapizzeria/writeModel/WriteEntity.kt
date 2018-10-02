package com.gamasoft.anticapizzeria.writeModel

import com.gamasoft.anticapizzeria.eventStore.*


data class OrderDetail(val itemId: String, val qty: Int)


interface EventComposable<T: Event> {
    fun compose(e: T): EventComposable<T>
}


sealed class Order(val id: String): EventComposable<OrderEvent>{
    override abstract fun compose(e: OrderEvent): Order
}

object emptyOrder: Order("") {
    override fun compose(e: OrderEvent) = when (e) {
        is Started -> NewOrder(e.phoneNum, emptyList())
        else -> this //ignore other events
    }
}

data class NewOrder(val phoneNum: String, val details: List<OrderDetail>): Order(phoneNum) {
    override fun compose(e: OrderEvent) = when (e) {
        is ItemAdded -> NewOrder(phoneNum, details.plus(OrderDetail(e.itemId, e.quantity)))
        is AddressAdded -> ReadyOrder(phoneNum, e.address, details)
        is Cancelled -> CancelledOrder(phoneNum)
        else -> this
    }
}

data class ReadyOrder(val phoneNum: String, val address: String, val details: List<OrderDetail>): Order(phoneNum) {
    override fun compose(e: OrderEvent) = when (e) {
        is ItemAdded -> ReadyOrder(phoneNum, address, details.plus(OrderDetail(e.itemId, e.quantity)))
        is AddressAdded -> ReadyOrder(phoneNum, e.address, details)
        is Cancelled -> CancelledOrder(phoneNum)
        is Confirmed -> ConfirmedOrder(phoneNum, address, details)
        else -> this
    }
}

data class ConfirmedOrder(val phoneNum: String, val address: String, val details: List<OrderDetail>): Order(phoneNum) {
    override fun compose(e: OrderEvent) = when (e) {
        is Refused -> RefusedOrder(phoneNum, e.reason)
        is Paid -> PaidOrder(phoneNum, e.totalPaid)
        else -> this
    }
}

data class PaidOrder(val phoneNum: String, val totalPaid: Double): Order(phoneNum) {
    override fun compose(e: OrderEvent) = this
}

data class CancelledOrder(val phoneNum: String): Order(phoneNum) {
    override fun compose(e: OrderEvent) = this
}

data class RefusedOrder(val phoneNum: String, val reason:String): Order(phoneNum) {
    override fun compose(e: OrderEvent) = this
}


sealed class Item(val id: String): EventComposable<ItemEvent>{
    override abstract fun compose(e: ItemEvent): Item
}

object emptyItem: Item("") {
    override fun compose(e: ItemEvent) = when (e) {
        is ItemCreated -> EnabledItem(e.itemId, e.desc, e.price)
        else -> this
    }
}

data class EnabledItem(val itemId: String, val name: String, val price: Double): Item(itemId) {
    override fun compose(e: ItemEvent) = when (e) {
        is ItemEdited -> EnabledItem(itemId, e.desc, e.price)
        is ItemDisabled -> DisabledItem(itemId, name, price)
        else -> this
    }
}

data class DisabledItem(val itemId: String, val name: String, val price: Double): Item(itemId) {
    override fun compose(e: ItemEvent) = when (e) {
        is ItemEnabled -> EnabledItem(itemId, name, price)
        else -> this
    }
}

