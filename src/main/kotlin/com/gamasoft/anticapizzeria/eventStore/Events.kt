package com.gamasoft.anticapizzeria.eventStore

import java.time.Instant


sealed class Event() {
    abstract fun key():String

    val created = Instant.now()
    val version = 0
}

sealed class OrderEvent(): Event(){
    abstract val phoneNum: String
    override fun key(): String = phoneNum
}

data class Started(override val phoneNum: String): OrderEvent()
data class ItemAdded(override val phoneNum: String, val itemId: String, val quantity: Int): OrderEvent()
data class ItemRemoved(override val phoneNum: String, val itemId: String): OrderEvent()
data class AddressAdded(override val phoneNum: String, val address: String): OrderEvent()
data class Confirmed(override val phoneNum: String): OrderEvent()
data class Cancelled(override val phoneNum: String): OrderEvent()
data class Paid(override val phoneNum: String, val totalPaid: Double): OrderEvent()
data class Refused(override val phoneNum: String, val reason: String): OrderEvent()


sealed class ItemEvent(): Event(){
    abstract val itemId: String
    override fun key(): String = itemId
}


data class ItemCreated(override val itemId: String, val desc: String, val price: Double): ItemEvent()
data class ItemDisabled(override val itemId: String): ItemEvent()
data class ItemEdited(override val itemId: String, val desc: String, val price: Double): ItemEvent()
data class ItemEnabled(override val itemId: String): ItemEvent()


