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

        fun process(q: Query<out Entity>): List<Entity> {
            return queryHandler.handle(q)
        }

    }




}