package com.gamasoft.anticapizzeria.readModel


enum class OrderStatus {new, ready, confirmed, paid, cancelled, refused}

data class OrderDetail(val itemName: String, val qty: Int): ReadEntity()



sealed class ReadEntity

data class ReadOrder(var status: OrderStatus, val phoneNum: String, var total: Double, var address: String?, val details: MutableList<OrderDetail>): ReadEntity()

data class ReadItem(var name: String, var price: Double, var enabled: Boolean): ReadEntity()
