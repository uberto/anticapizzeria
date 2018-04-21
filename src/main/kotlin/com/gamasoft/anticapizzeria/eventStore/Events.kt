package com.gamasoft.anticapizzeria.eventStore



sealed class Event {
    abstract fun pk(): String
}

abstract class OrderEvent(phoneNum: String): Event(){

    private val pk = "Order-" + phoneNum

    override fun pk(): String {
        return pk
    }
}
data class OrderStarted(val phoneNum: String): OrderEvent(phoneNum)
data class ItemAdded(val phoneNum: String, val item: String, val quantity: Int): OrderEvent(phoneNum)
data class ItemRemoved(val phoneNum: String, val item: String): OrderEvent(phoneNum)
data class AddressAdded(val phoneNum: String, val address: String): OrderEvent(phoneNum)
data class Confirmed(val phoneNum: String): OrderEvent(phoneNum)
data class Cancelled(val phoneNum: String): OrderEvent(phoneNum)
data class Delivered(val phoneNum: String): OrderEvent(phoneNum)
data class Paid(val phoneNum: String, val price: Double): OrderEvent(phoneNum)

abstract class ItemEvent(itemId: String): Event(){

    private val pk = "Item-" + itemId

    override fun pk(): String {
        return pk
    }
}
data class ItemCreated(val itemId: String, val desc: String, val price: Double): ItemEvent(itemId)
data class ItemDisabled(val itemId: String): ItemEvent(itemId)
data class ItemEdited(val itemId: String, val desc: String, val price: Double): ItemEvent(itemId)
data class ItemEnabled(val itemId: String): ItemEvent(itemId)


