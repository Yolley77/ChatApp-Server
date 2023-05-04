package com.chatapp.plugins

import com.chatapp.Connection
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.time.*
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                val userLogin = (incoming.receive() as? Frame.Text)?.readText()
                thisConnection.userLogin = setFreeLogin(userLogin, connections)
                connections.forEach {
                    it.session.send("${thisConnection.userLogin} подключился! Теперь нас ${connections.count()}.")
                }
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    println("RECEIVING <<<--- --- $receivedText")

                    val textWithUsername = "[${thisConnection.userLogin}]: $receivedText"
                    println("SENDING --- --->>> $textWithUsername")
                    connections.forEach {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                connections.forEach {
                    it.session.send("${thisConnection.userLogin} отключился, нас ${connections.count() - 1}.")
                }
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}

fun setFreeLogin(login: String?, connections: MutableSet<Connection>): String {
    connections.forEach { connection ->
        if (connection.userLogin == login) return connection.name
    }
    return login ?: connections.last().name
}
