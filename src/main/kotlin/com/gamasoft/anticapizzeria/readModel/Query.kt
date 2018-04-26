package com.gamasoft.anticapizzeria.readModel

sealed class Query<T: ReadEntity>

sealed class OrderQuery: Query<ReadOrder>()
object GetAllOpenOrders: OrderQuery()
data class GetOrder(val phoneNum: String): OrderQuery()
object GetBiggestOrder: OrderQuery()

sealed class ItemQuery: Query<ReadItem>()
data class GetItem(val itemId: String): ItemQuery()
object GetAllActiveItems: ItemQuery()
