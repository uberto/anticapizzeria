package com.gamasoft.anticapizzeria

sealed class Entity

data class Order(val item: String, val qty: Int): Entity()

data class Item(val name: String, val price: Double): Entity()
