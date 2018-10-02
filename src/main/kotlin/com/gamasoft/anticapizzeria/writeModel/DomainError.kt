package com.gamasoft.anticapizzeria.writeModel

sealed class DomainError(val msg: String)

data class ItemError(val e: String, val item: Item): DomainError(e)
data class OrderError(val e: String, val order: Order): DomainError(e)