package com.gamasoft.anticapizzeria.eventStore

import java.time.Instant


sealed class Event(val pk: String) {
    val created = Instant.now()
    val version = 0
}

sealed class OrderEvent(val key: String): Event(key)

sealed class ItemEvent(val key: String): Event(key)


data class Started(val phoneNum: String): OrderEvent(phoneNum)
data class ItemAdded(val phoneNum: String, val itemId: String, val quantity: Int): OrderEvent(phoneNum)
data class ItemRemoved(val phoneNum: String, val itemId: String): OrderEvent(phoneNum)
data class AddressAdded(val phoneNum: String, val address: String): OrderEvent(phoneNum)
data class Confirmed(val phoneNum: String): OrderEvent(phoneNum)
data class Cancelled(val phoneNum: String): OrderEvent(phoneNum)
data class Paid(val phoneNum: String, val totalPaid: Double): OrderEvent(phoneNum)
data class Refused(val phoneNum: String, val reason: String): OrderEvent(phoneNum)

data class ItemCreated(val itemId: String, val desc: String, val price: Double): ItemEvent(itemId)
data class ItemDisabled(val itemId: String): ItemEvent(itemId)
data class ItemEdited(val itemId: String, val desc: String, val price: Double): ItemEvent(itemId)
data class ItemEnabled(val itemId: String): ItemEvent(itemId)


