package com.gamasoft.anticapizzeria

interface Query{

}

sealed class OrderQuery:Query {
    object GetAllOpenOrders: OrderQuery()
    data class GetOrderStatus(val phoneNum: String): OrderQuery()
    object GetBiggestOrder: OrderQuery()
}

sealed class ItemQuery:Query {
    data class GetItem(val itemId: String): ItemQuery()
    object GetAllActiveItems: ItemQuery()
}