package pl.blokaj.pokerbro.backend.client.ktor

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


data class Lobby(val hostname: String, val ip: String, val port: Int)

private const val SEARCHING_PORT = 8888

fun CoroutineScope.startGameSearching(lobbyFlow: MutableSharedFlow<Lobby>): Job {
    val selectorManager = SelectorManager()
    val lobbySet = ThreadSafeSet<Lobby>()

    return launch {
        val socket = aSocket(selectorManager)
            .udp()
            .bind(InetSocketAddress("10.0.2.15", SEARCHING_PORT), configure = { this.broadcast = true })

        val receiveChannel = socket.incoming

        while (isActive) {
            val datagram = receiveChannel.receive()
            val message = datagram.packet.readText()
            val address = datagram.address as InetSocketAddress
            println("Message received")
            // "Hostname:$serverHost;Gameport:$gamePort"
            val split = message.split(';')
            if (split.size == 2) {
                val hostNameFormat = split[0].split(':')
                val gamePortFormat = split[1].split(':')
                if (hostNameFormat[0] == "Hostname" && gamePortFormat[0] == "Gameport"
                    && hostNameFormat.size == 2 && gamePortFormat.size == 2
                ) {
                    val hostName = hostNameFormat[1]
                    val gamePort: Int? = gamePortFormat[1].toIntOrNull()
                    if (gamePort != null) {
                        val newLobby = Lobby(hostName, address.hostname, gamePort)
                        if (lobbySet.add(newLobby)) {
                            lobbyFlow.emit(newLobby)
                        }
                    }
                }
            }
        }
    }
}