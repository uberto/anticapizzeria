package com.gamasoft.anticapizzeria.application

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.gamasoft.anticapizzeria.eventStore.AddressAdded
import com.gamasoft.anticapizzeria.readModel.*
import com.gamasoft.anticapizzeria.writeModel.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ApplicationTest {

    val application = Application()

    @BeforeEach
    fun setUp(){
        application.start()
    }

    @Test
    fun newOrder() {
        val pn = "101"
        val r = application.process(StartOrder(pn))
        assert(r).isEqualTo("Ok")

        Thread.sleep(100)

        val os = application.process(GetOrder(pn))

        assert(os).hasSize(1)
        assert((os[0] as Order) ).isEqualTo(Order(OrderStatus.new, pn, 0.0, null, mutableListOf()))


        val eos = application.process(GetOrder("***"))
        assert(eos).isEmpty()
    }

    @Test
    fun newItem() {
        val id = "CAPRI"
        val r = application.process(CreateItem(id, "pizza capricciosa", 7.5))
        assert(r).isEqualTo("Ok")

        Thread.sleep(100)

        val os = application.process(GetItem(id))

        assert(os).hasSize(1)
        assert((os[0] as Item) ).isEqualTo(Item("pizza capricciosa", 7.5, true))

        val eos = application.process(GetItem("***"))
        assert(eos).isEmpty()
    }

    @Test
    fun deliverTwoMargherita() {
        val pn = "123"
        val commands = listOf(
                CreateItem("MAR", "pizza margherita", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "MAR", 2),
                AddAddress(pn, "Oxford Circus, 4"),
                Confirm(pn),
                Deliver(pn))

        for (c in commands) {
            val r = application.process(c)
            assert(r).isEqualTo("Ok")
        }

        Thread.sleep(100)

        val os = application.process(GetOrder(pn))
        assert(os).hasSize(1)
        assert((os[0] as Order) ).isEqualTo(smallOrder(pn))


    }

    private fun smallOrder(pn: String) = Order(OrderStatus.leftForDelivery, pn, 12.0, "Oxford Circus, 4", mutableListOf(OrderDetail("pizza margherita", 2)))

    @Test
    fun longer() {

        val pn = "0755 123456"
        val commands = listOf(
                StartOrder(pn),
                AddItem(pn, "margherita", 2),
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

    @Test
    fun changingPriceAfterConfirm(){}

    @Test
    fun cancelOrder(){}

}