package com.gamasoft.anticapizzeria.readModel

sealed class Query

object GetAllOpenOrders: Query()
data class GetOrder(val phoneNum: String): Query()
object GetBiggestOrder: Query()

data class GetItem(val itemId: String): Query()
object GetAllActiveItems: Query()


