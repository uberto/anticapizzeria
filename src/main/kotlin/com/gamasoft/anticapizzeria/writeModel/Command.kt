package com.gamasoft.anticapizzeria.writeModel


sealed class Command
data class StartOrder(val phoneNum: String): Command()
data class AddItem(val phoneNum: String, val itemId: String,
                   val quantity: Int): Command()
data class RemoveItem(val phoneNum: String, val itemId: String): Command()
data class AddAddress(val phoneNum: String, val address: String): Command()
data class Confirm(val phoneNum: String): Command()
data class Cancel(val phoneNum: String): Command()
data class Pay(val phoneNum: String, val price: Double): Command()
data class Refuse(val phoneNum: String, val reason: String): Command()

data class CreateItem(val itemId: String, val desc: String,
                      val price: Double): Command()
data class DisableItem(val itemId: String): Command()
data class EditItem(val itemId: String, val desc: String,
                    val price: Double): Command()
data class EnableItem(val itemId: String): Command()
