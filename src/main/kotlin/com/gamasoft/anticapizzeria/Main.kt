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


    val application = Application()

    val pn = "0755 123456"
    val commands = listOf(
            StartOrder(pn),
            AddItem(pn, "pizza margherita", 2),
            Confirm(pn))

    for (c in commands) {
        val r = application.process(c)
        println("Processed $c with result $r")
    }

    val queries = listOf(
            GetAllOpenOrders,
            GetOrder(pn),
            GetAllActiveItems)


    for (q in queries) {
        val r = application.process(q)

        println("Processed $q with result $r")

    }


}