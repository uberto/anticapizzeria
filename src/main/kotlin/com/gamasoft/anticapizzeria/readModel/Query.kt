package com.gamasoft.anticapizzeria.readModel

sealed class Query<T: Entity>

sealed class OrderQuery: Query<Order>()
object GetAllOpenOrders: OrderQuery()
data class GetOrder(val phoneNum: String): OrderQuery()
object GetBiggestOrder: OrderQuery()


sealed class ItemQuery: Query<Item>()
data class GetItem(val itemId: String): ItemQuery()
object GetAllActiveItems: ItemQuery()
