package com.gamasoft.anticapizzeria.eventStore



data class OrderDetail(val itemId: String, val qty: Int)


sealed class Order()
data class NewOrder(val phoneNum: String, val details: List<OrderDetail>): Order()
data class ReadyOrder(val phoneNum: String, val address: String, val details: List<OrderDetail>):Order()
data class ConfirmedOrder(val phoneNum: String, val address: String, val details: List<OrderDetail>):Order()
data class DeliveringOrder(val phoneNum: String, val address: String, val details: List<OrderDetail>):Order()
data class PaidOrder(val phoneNum: String, val totalPaid: Double):Order()
data class CancelledOrder(val phoneNum: String):Order()
data class RefusedOrder(val phoneNum: String, val reason:String):Order()


sealed class Item()
data class EnabledItem(val id: String, val name: String, val price: Double):Item()
data class DisabledItem(val id: String, val name: String, val price: Double):Item()

