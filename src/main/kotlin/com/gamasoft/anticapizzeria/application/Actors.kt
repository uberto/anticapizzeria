package com.gamasoft.anticapizzeria.application

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor


fun <T> createActor(block: suspend (T)->Unit): SendChannel<T> {
    return actor {

        for (qm in channel) {
            block(qm)
        }
    }
}
