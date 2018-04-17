package com.gamasoft.anticapizzeria

interface Command {

}

sealed class OrderCommand:Command {
    data class StartOrder(val phoneNum: String): OrderCommand()
    data class AddItem(val phoneNum: String, val item: String, val quantity: Int): OrderCommand()
    data class RemoveItem(val phoneNum: String, val item: String): OrderCommand()
    data class AddAddress(val phoneNum: String, val address: String): OrderCommand()
    data class Confirm(val phoneNum: String): OrderCommand()
    data class Cancel(val phoneNum: String): OrderCommand()
    data class Deliver(val phoneNum: String): OrderCommand()
    data class Pay(val phoneNum: String, val price: Double): OrderCommand()
}

sealed class ItemCommand:Command {
    data class CreateItem(val itemId: String, val desc: String, val price: Double): ItemCommand()
    data class DisableItem(val itemId: String): ItemCommand()
    data class EditItem(val itemId: String, val desc: String, val price: Double): ItemCommand()
    data class EnableItem(val itemId: String): ItemCommand()
}
