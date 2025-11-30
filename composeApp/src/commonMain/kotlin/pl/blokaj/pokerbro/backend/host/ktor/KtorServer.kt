package pl.blokaj.pokerbro.backend.host.ktor

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.*
import kotlin.collections.MutableSet
import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.cio.*
import io.ktor.server.routing.routing
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeText
import io.ktor.utils.io.readText
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.Frame.Text
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import pl.blokaj.pokerbro.serverLogBus
import pl.blokaj.pokerbro.backend.shared.Event


const val GAME_PORT = 9000
const val UDP_PORT = 8888


fun startServer(hostName: String, scope: CoroutineScope) {
    val bus = EventBus()
    serverLogBus.logs.tryEmit("Starting udp service and network bus\n")
    val discoveryService = scope.startUdpDiscoveryService(hostName, UDP_PORT, GAME_PORT)
    val connectionManager = ConnectionManager(scope)
    val eventBridge = scope.startBusToNetwork(connectionManager, bus)
    embeddedServer(CIO, configure = {
        connectors.add(EngineConnectorBuilder().apply {
            host = "127.0.0.1"
            port = GAME_PORT
        })
    }) {
        serverLogBus.logs.tryEmit("Starting websockets\n")
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

fun CoroutineScope.startUdpDiscoveryService(serverHost: String, udpPort: Int, gamePort: Int): Job {
    val selectorManager = SelectorManager(Dispatchers.IO)

    return launch {
        try {
            println("Binding socket in 0.0.0.0 and port $udpPort\n")

            val socket = aSocket(selectorManager)
                .udp()
                .bind(InetSocketAddress("0.0.0.0", udpPort), configure = { this.broadcast = true })


            val sendChannel = socket.outgoing
            val message = "Hostname:$serverHost;Gameport:$gamePort"
            val broadcastAddress = InetSocketAddress("255.255.255.255", udpPort)

            while (isActive) {
                println("Sending message $message")
                serverLogBus.logs.emit("Sending message $message")
                val packet = buildPacket({ writeText(message) })
                val datagram = Datagram(packet, broadcastAddress)
                sendChannel.send(datagram)
                delay(5.seconds)
            }
        } finally {
            selectorManager.close()
        }
    }
}

