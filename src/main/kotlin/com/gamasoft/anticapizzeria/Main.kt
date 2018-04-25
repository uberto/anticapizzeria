package com.gamasoft.anticapizzeria

import com.gamasoft.anticapizzeria.application.Application
import com.gamasoft.anticapizzeria.readModel.GetAllActiveItems
import com.gamasoft.anticapizzeria.readModel.GetAllOpenOrders
import com.gamasoft.anticapizzeria.readModel.GetOrder
import com.gamasoft.anticapizzeria.writeModel.AddItem
import com.gamasoft.anticapizzeria.writeModel.Confirm
import com.gamasoft.anticapizzeria.writeModel.StartOrder

fun main(args: Array<String>) {

    println("Antica Pizzeria! Best Pizza outside Naples")


    val application = Application().apply {

        val pn = "0755 123456"
        listOf(
            StartOrder(pn),
            AddItem(pn, "pizza margherita", 2),
            Confirm(pn)).processAllInSync()

        listOf(
            GetAllOpenOrders,
            GetOrder(pn),
            GetAllActiveItems).forEach {
            println("Processed Query $it with result ${it.process()}")
        }
    }


}