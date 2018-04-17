package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking


class CommandHandler {

    val sendChannel: SendChannel<Command>
    init{
        sendChannel = actor{
            for (c in channel) {
                println("Processing $c")
                delay(10)
            }

            println("done!")
        }
    }

    fun handle(c: Command):String {

        runBlocking {
            sendChannel.send(c)

        }
        return "Ok"
    }

}
