package com.gamasoft.anticapizzeria.application

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.gamasoft.anticapizzeria.functional.Valid
import com.gamasoft.anticapizzeria.readModel.*
import com.gamasoft.anticapizzeria.readModel.ReadItem
import com.gamasoft.anticapizzeria.readModel.ReadOrder
import com.gamasoft.anticapizzeria.readModel.OrderDetail
import com.gamasoft.anticapizzeria.writeModel.*
import kotlinx.coroutines.experimental.runBlocking
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
        application.apply{
            val r = StartOrder(pn).process()

            Thread.sleep(10) //async so we have to wait

            val os = GetOrder(pn).process()

            assert(os).hasSize(1)
            assert((os[0] as ReadOrder) ).isEqualTo(ReadOrder(OrderStatus.new, pn, 0.0, null, mutableListOf()))


            val eos = GetOrder("***").process()
            assert(eos).isEmpty()
        }
    }

    @Test
    fun newItem() {

        val id = "CAPRI"

        application.apply {
            val r = CreateItem(id, "pizza capricciosa", 7.5).process()

            runBlocking {
                val errors =r.await()
                assert(errors is Valid)
            }

            Thread.sleep(10) //Query model eventual consistency
            val os = GetItem(id).process()
            assert(os).hasSize(1)
            assert((os[0] as ReadItem)).isEqualTo(ReadItem("pizza capricciosa", 7.5, true))

            val eos = GetItem("***").process()
            assert(eos).isEmpty()
        }
    }

    @Test
    fun deliverTwoMargherita() {
        val pn = "123"
        application.apply {
            val errors = listOf(
                CreateItem("MAR", "pizza margherita", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "MAR", 2),
                AddAddress(pn, "Oxford Circus, 4"),
                Confirm(pn),
                Dispatch(pn, "Joe"),
                Pay(pn, 12.0)).processAllInSync()

            assert(errors).isEmpty()

            Thread.sleep(10) //Query model eventual consistency

            val os = GetOrder(pn).process()
            assert(os).hasSize(1)
            assert((os[0] as ReadOrder) ).isEqualTo(smallOrder(pn))
        }
    }


    @Test
    fun twoOrdersAtSameTime() {

        val pn1 = "123456"
        val pn2 = "123457"
        application.apply {
            val errors = listOf(
                    CreateItem("MAR", "pizza margherita", 6.0),
                    CreateItem("CAP", "pizza capricciosa", 7.5),
                    CreateItem("COK", "soda can", 2.0),

                    StartOrder(pn1),
                    AddItem(pn1, "MAR", 2),
                    AddAddress(pn1, "12, Long St."),
                    StartOrder(pn2),
                    AddItem(pn2, "COK", 3),
                    AddItem(pn2, "CAP", 3)
            ).processAllInSync()

            assert(errors).isEmpty()

            Thread.sleep(10) //Query model eventual consistency

            val oo = GetAllOpenOrders.process()
            assert(oo).hasSize(2)
            val ai = GetAllActiveItems.process()
            assert(ai).hasSize(3)
            val o1 = GetOrder(pn1).process()
            assert(o1).hasSize(1)
            val order1 = o1[0] as ReadOrder
            assert(order1.total).isEqualTo(12.0)
            assert(order1.status).isEqualTo(OrderStatus.ready)
            val o2 = GetOrder(pn2).process()
            assert(o2).hasSize(1)
            val order2 = o2[0] as ReadOrder
            assert(order2.total).isEqualTo(28.5)
            assert(order2.status).isEqualTo(OrderStatus.new)

            val bo = GetBiggestOrder.process()
            assert(o2).hasSize(1)

            assert(bo[0] as ReadOrder).isEqualTo(order2)

        }
    }

    @Test
    fun orderRefused() {
        val pn = "123"
        application.apply {
            val errors = listOf(
                    CreateItem("MAR", "pizza margherita", 6.0 ),
                    StartOrder(pn),
                    AddItem(pn, "MAR", 2),
                    AddAddress(pn, "Oxford Circus, 4"),
                    Confirm(pn),
                    Dispatch(pn, "Frank"),
                    Refuse(pn, "Pizza arrived cold")).processAllInSync()

            assert(errors).isEmpty()

            Thread.sleep(10) //Query model eventual consistency


            val os = GetOrder(pn).process()
            assert(os).hasSize(1)
            assert((os[0] as ReadOrder).status ).isEqualTo(OrderStatus.refused)
        }
    }

    @Test
    fun changingPriceAfterConfirm(){
        val pn = "123"
        application.apply {
            val errors = listOf(
                CreateItem("CAL", "calzone", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "CAL", 3),
                AddAddress(pn, "Oxford Circus, 6"),
                Confirm(pn),
                EditItem("CAL", "calzone", 7.0 )
                ).processAllInSync()

            assert(errors).isEmpty()

            Thread.sleep(10) //Query model eventual consistency


            val os = GetOrder(pn).process()
            assert(os).hasSize(1)
            assert((os[0] as ReadOrder).total ).isEqualTo(18.0)

            val il = GetItem("CAL").process()
            assert(il).hasSize(1)
            assert((il[0] as ReadItem) ).isEqualTo(ReadItem("calzone", 7.0, true))
        }
    }

    @Test
    fun cancelOrder(){
        val pn = "567"
        application.apply {
            val errors = listOf(
                CreateItem("MAR", "pizza margherita", 6.0 ),
                StartOrder(pn),
                AddItem(pn, "MAR", 2),
                AddAddress(pn, "Oxford Circus, 4"),
                Cancel(pn)).processAllInSync()


            assert(errors).isEmpty()
            val oo = GetAllOpenOrders.process()
            assert(oo).hasSize(0)
            val os = GetOrder(pn).process()
            assert(os).hasSize(1)
            assert((os[0] as ReadOrder).status ).isEqualTo(OrderStatus.cancelled)
        }
    }

    @Test
    fun disableAndReenableItems(){
        val itemId = "MAR"
        val itemId2 = "CAPRI"
        application.apply {
            val errors = listOf(
                CreateItem(itemId, "pizza margherita", 6.0 ),
                CreateItem(itemId2, "pizza capricciosa", 8.0 ),
                DisableItem(itemId),
                DisableItem(itemId2),
                EnableItem(itemId)).processAllInSync()

            assert(errors).isEmpty()

            Thread.sleep(10) //Query model eventual consistency


            val ai = GetAllActiveItems.process()
            assert(ai).hasSize(1)
        }
    }


    @Test
    fun removeItems(){
        val pn = "123"
        val itemId = "MAR"
        val itemId2 = "CAPRI"
        application.apply {
            val errors = listOf(
                    CreateItem(itemId, "pizza margherita", 6.0 ),
                    CreateItem(itemId2, "pizza capricciosa", 8.0 ),
                    StartOrder(pn),
                    AddItem(pn, itemId, 2),
                    AddItem(pn, itemId2, 1),
                    AddItem(pn, itemId2, 3),
                    RemoveItem(pn, itemId2)).processAllInSync()

            assert(errors).isEmpty()

            val ai = GetOrder(pn).process()
            assert(ai).hasSize(1)
            assert((ai[0] as ReadOrder).details).hasSize(1)
            assert((ai[0] as ReadOrder).details[0]).isEqualTo(OrderDetail("pizza margherita", 2))
        }
    }


    @Test
    fun cannotCancelAfterConfirm() {
        val pn = "567"
        application.apply {
            val errors = listOf(
                    CreateItem("MAR", "pizza margherita", 6.0 ),
                    StartOrder(pn),
                    AddItem(pn, "MAR", 2),
                    AddAddress(pn, "Oxford Circus, 4"),
                    Confirm(pn),
                    Cancel(pn)).processAllInSync()

            assert(errors).hasSize(1)
            assert(errors[0].msg).isEqualTo("Order cannot be cancelled now! ConfirmedOrder(phoneNum=567, address=Oxford Circus, 4, details=[OrderDetail(itemId=MAR, qty=2)])")

        }
    }

    @Test
    fun cannotAddNonExistingItem() {
        val pn = "678"
        val itemId = "MAR"
        application.apply {
            val errors = listOf(
                    StartOrder(pn),
                    AddItem(pn, itemId, 2)
            ).processAllInSync()


            assert(errors).hasSize(1)
            assert(errors[0].msg).isEqualTo("Cannot add non existing item! MAR")
        }
    }


    @Test
    fun cannotAddDisabledItem() {
        val pn = "678"
        val itemId = "MAR"
        application.apply {
            val errors = listOf(
                    CreateItem(itemId, "pizza margherita", 6.0 ),
                    StartOrder(pn),
                    DisableItem(itemId),
                    AddItem(pn, itemId, 2)
            ).processAllInSync()


            assert(errors).hasSize(1)
            assert(errors[0].msg).isEqualTo("Cannot add disabled item! DisabledItem(itemId=MAR, name=pizza margherita, price=6.0)")
        }
    }

    private fun smallOrder(pn: String) = ReadOrder(OrderStatus.paid, pn, 12.0, "Oxford Circus, 4", mutableListOf(OrderDetail("pizza margherita", 2)))



}

