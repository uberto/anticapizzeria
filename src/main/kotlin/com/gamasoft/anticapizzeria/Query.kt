package com.gamasoft.anticapizzeria

interface Query<T>{
}

sealed class OrderQuery:Query<Order> {
    object GetAllOpenOrders: OrderQuery()
    data class GetOrderStatus(val phoneNum: String): OrderQuery()
    object GetBiggestOrder: OrderQuery()


}

sealed class ItemQuery:Query<Item> {
    data class GetItem(val itemId: String): ItemQuery()
    object GetAllActiveItems: ItemQuery()
}