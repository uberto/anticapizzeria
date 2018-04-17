package com.gamasoft.anticapizzeria

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.runBlocking


class CommandHandler {

    val sendChannel: SendChannel<Command>
    init{
        sendChannel = actor{

        }
    }

    fun handle(c: Command):String {

        runBlocking {
            sendChannel.send(c)

        }
        return "Ok"
    }

}
