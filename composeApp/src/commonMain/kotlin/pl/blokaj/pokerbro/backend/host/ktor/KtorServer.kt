package pl.blokaj.pokerbro.backend.host.ktor

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.*
import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.cio.*
import io.ktor.server.routing.routing
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeText
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.serverLogBus
import pl.blokaj.pokerbro.shared.Event


const val GAME_PORT = 50001
const val UDP_PORT = 57286





fun startServer(lanNetworkManager: LanNetworkManager, hostName: String, scope: CoroutineScope) {
    val ip = lanNetworkManager.getIpAddresses()
    val bus = EventBus()
    val discoveryService = scope.startUdpDiscoveryService(lanNetworkManager, hostName)
    val connectionManager = ConnectionManager(scope)
    val eventBridge = scope.startBusToNetwork(connectionManager, bus)
    embeddedServer(CIO, configure = {
        connectors.add(EngineConnectorBuilder().apply {
            host = "127.0.0.1"
            port = GAME_PORT
        })
    }) {
        module(bus)
    }.start(wait = false)

}

fun handleEvent() {
    TODO()
}

fun Application.module(bus: EventBus) {
    install(WebSockets)

    routing {
        webSocket("/game") {
            for (frame in incoming) {
                val textFrame = frame as? Frame.Text ?: continue
                val event = Json.decodeFromString<Event>(textFrame.readText())
                serverLogBus.logs.tryEmit("Event ${textFrame.readText()}")
                handleEvent()
                bus.produceEvent(event)
            }
        }
    }
}

fun CoroutineScope.startBusToNetwork(connectionManager: ConnectionManager, eventBus: EventBus): Job {
    return launch {
        eventBus.events.collect { event ->
            val jsonString = Json.encodeToString<Event>(event)
            val frame = Frame.Text(jsonString)

            connectionManager.broadcast(frame)
        }
    }
}

fun CoroutineScope.startUdpDiscoveryService(lanNetworkManager: LanNetworkManager, serverHost: String): Job {
    return launch {
        lanNetworkManager.withBroadcastLock {
            val selectorManager = SelectorManager(Dispatchers.IO)
            try {
                val ipAddresses = lanNetworkManager.getIpAddresses()
                val broadcastAddresses = lanNetworkManager
                    .getBroadcastAddresses()
                    .map { InetSocketAddress(it, UDP_PORT) }
                val sockets = ipAddresses.map { ip ->
                    aSocket(selectorManager)
                        .udp()
                        .bind(InetSocketAddress(ip, UDP_PORT), configure = { this.broadcast = true })
                }
                val channels = sockets.map { it.outgoing }
                val message = "Hostname:$serverHost;Gameport:$GAME_PORT"
                var counter = 0

                while (isActive) {
                    for (i in 0..<ipAddresses.size) {
                        println("Sending to ${ipAddresses[i]}")
                        val packet = buildPacket({ writeText(message) })
                        val datagram = Datagram(packet, broadcastAddresses[i])
                        channels[i].send(datagram)
                    }
                    counter += 1
                    delay(5.seconds)
                }
            } finally {
                selectorManager.close()
            }

        }
    }
}

