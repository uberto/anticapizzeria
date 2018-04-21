package com.gamasoft.anticapizzeria.eventStore

import java.time.Instant


sealed class Event(val pk: String) {
    val created = Instant.now()
    val version = 0

    fun pk(): String {
        return pk
    }
}



data class OrderStarted(val phoneNum: String): Event(phoneNum)
data class ItemAdded(val phoneNum: String, val itemId: String, val quantity: Int): Event(phoneNum)
data class ItemRemoved(val phoneNum: String, val itemId: String): Event(phoneNum)
data class AddressAdded(val phoneNum: String, val address: String): Event(phoneNum)
data class Confirmed(val phoneNum: String): Event(phoneNum)
data class Cancelled(val phoneNum: String): Event(phoneNum)
data class DeliverStarted(val phoneNum: String): Event(phoneNum)
data class Paid(val phoneNum: String, val price: Double): Event(phoneNum)
data class Refused(val phoneNum: String, val reason: String): Event(phoneNum)


data class ItemCreated(val itemId: String, val desc: String, val price: Double): Event(itemId)
data class ItemDisabled(val itemId: String): Event(itemId)
data class ItemEdited(val itemId: String, val desc: String, val price: Double): Event(itemId)
data class ItemEnabled(val itemId: String): Event(itemId)


