package com.gamasoft.anticapizzeria



sealed class Application{

    object application: Application() {

        val commandHandler = CommandHandler()
        val queryHandler = QueryHandler()

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

        inline fun <reified T> process(q: Query<T>): List<T> {
            return queryHandler.handle(q)
        }

    }




}