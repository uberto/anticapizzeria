package com.gamasoft.anticapizzeria.readModel


enum class OrderStatus {new, open, delivery, paid, cancelled}

data class OrderDetails(val itemName: String, val qty: Int): Entity()



sealed class Entity

data class Order(val status: OrderStatus, val phoneNum: String, val total: Double, val details: Array<OrderDetails>): Entity()

data class Item(val name: String, val price: Double): Entity()
