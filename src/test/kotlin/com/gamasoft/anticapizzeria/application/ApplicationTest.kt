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
        application.processAll(listOf(
                CreateItem("MAR", "pizza margherita", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "MAR", 2),
                AddAddress(pn, "Oxford Circus, 4"),
                Confirm(pn),
                Deliver(pn)))

        Thread.sleep(10)

        val os = application.process(GetOrder(pn))
        assert(os).hasSize(1)
        assert((os[0] as Order) ).isEqualTo(smallOrder(pn))


    }

    @Test
    fun twoOrders() {

        val pn1 = "123456"
        val pn2 = "123457"
        application.processAll( listOf(
                CreateItem("MAR", "pizza margherita", 6.0 ),
                CreateItem("CAP", "pizza capricciosa", 7.5 ),
                CreateItem("COK", "soda can", 2.0 ),

                StartOrder(pn1),
                AddItem(pn1, "MAR", 2),
                AddAddress(pn1, "12, Long St."),
                StartOrder(pn2),
                AddItem(pn2, "COK", 3),
                AddItem(pn2, "CAP", 3))
        )


        Thread.sleep(10)

        val oo = application.process(GetAllOpenOrders)
        assert(oo).hasSize(2)
        val ai = application.process(GetAllActiveItems)
        assert(ai).hasSize(3)
        val o1 = application.process(GetOrder(pn1))
        assert(o1).hasSize(1)
        val order1 = o1[0] as Order
        assert(order1.total ).isEqualTo(12.0)
        assert(order1.status ).isEqualTo(OrderStatus.ready)
        val o2 = application.process(GetOrder(pn2))
        assert(o2).hasSize(1)
        val order2 = o2[0] as Order
        assert(order2.total ).isEqualTo(28.5)
        assert(order2.status ).isEqualTo(OrderStatus.new)

        val bo = application.process(GetBiggestOrder)
        assert(o2).hasSize(1)

        assert(bo[0] as Order).isEqualTo(order2)


    }

    @Test
    fun changingPriceAfterConfirm(){
        val pn = "123"
        application.processAll( listOf(
                CreateItem("CAL", "calzone", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "CAL", 3),
                AddAddress(pn, "Oxford Circus, 6"),
                Confirm(pn),
                EditItem("CAL", "calzone", 7.0 )
                ))

        Thread.sleep(10)

        val os = application.process(GetOrder(pn))
        assert(os).hasSize(1)
        assert((os[0] as Order).total ).isEqualTo(18.0)

        val il = application.process(GetItem("CAL"))
        assert(il).hasSize(1)
        assert((il[0] as Item) ).isEqualTo(Item("calzone", 7.0, true))

    }

    @Test
    fun cancelOrder(){
        val pn = "567"
        application.processAll(listOf(
                CreateItem("MAR", "pizza margherita", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "MAR", 2),
                AddAddress(pn, "Oxford Circus, 4"),
                Cancel(pn)))
        val oo = application.process(GetAllOpenOrders)
        assert(oo).hasSize(0)
        val os = application.process(GetOrder(pn))
        assert(os).hasSize(1)
        assert((os[0] as Order).status ).isEqualTo(OrderStatus.cancelled)
    }



    private fun smallOrder(pn: String) = Order(OrderStatus.leftForDelivery, pn, 12.0, "Oxford Circus, 4", mutableListOf(OrderDetail("pizza margherita", 2)))

}

