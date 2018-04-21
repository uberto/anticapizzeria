package com.gamasoft.anticapizzeria.readModel


enum class OrderStatus {new, ready, confirmed, leftForDelivery, paid, cancelled, refused}

data class OrderDetail(val itemName: String, val qty: Int): Entity()



sealed class Entity

data class Order(var status: OrderStatus, val phoneNum: String, var total: Double, var address: String?, val details: MutableList<OrderDetail>): Entity()

data class Item(var name: String, var price: Double, var enabled: Boolean): Entity()
