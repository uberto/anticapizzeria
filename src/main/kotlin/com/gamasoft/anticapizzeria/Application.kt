package com.gamasoft.anticapizzeria



sealed class Application{

    object application: Application() {

        private val commandHandler = CommandHandler()
        private val queryHandler = QueryHandler()

//    fun start() {
//
//    }
//
//    fun stop() {
//
//    }

        fun process(c: Command): String {
            return commandHandler.handle(c)
        }

        fun process(q: Query): String {
            return queryHandler.handle(q)
        }

    }




}