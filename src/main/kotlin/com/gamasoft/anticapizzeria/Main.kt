package com.gamasoft.anticapizzeria

fun main(args: Array<String>) {

    println("Antica Pizzeria! Best Pizza outside Naples")


//    Application.application.start()

    val pn = "0755 123456"
    val commands = listOf(
            OrderCommand.StartOrder(pn),
            OrderCommand.AddItem(pn, "pizza margherita", 2),
            OrderCommand.Confirm(pn))

    for (c in commands) {
        val r = Application.application.process(c)
        println("Processed $c with result $r")
    }

    val queries = listOf(
            OrderQuery.GetAllOpenOrders,
            OrderQuery.GetOrderStatus(pn),
            ItemQuery.GetAllActiveItems)

    for (q in queries) {
        val r = Application.application.process(q)
        println("Processed $q with result $r")
    }


}