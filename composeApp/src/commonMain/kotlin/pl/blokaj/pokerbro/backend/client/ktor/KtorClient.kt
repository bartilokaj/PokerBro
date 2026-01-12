package pl.blokaj.pokerbro.backend.client.ktor

import co.touchlab.kermit.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.utility.ThreadSafeMap
import pl.blokaj.pokerbro.utility.ThreadSafeSet

private const val SEARCHING_PORT = 57286

class KtorClient(
    private val lanNetworkManager: LanNetworkManager
) {
    data class Lobby(val lobbyName: String, val ip: String, val port: Int)
    private val log = Logger.withTag("Client")
    private val foundLobbies = ThreadSafeSet<Lobby>()
    private val lobbyMap = ThreadSafeMap<Int, Lobby>()

    fun CoroutineScope.startGameSearching(
        lanNetworkManager: LanNetworkManager,
        lobbyNameFlow: MutableSharedFlow<Pair<Int, String>>,
    ): Job {
        return launch {
            lanNetworkManager.withBroadcastLock {
                val selectorManager = SelectorManager(Dispatchers.IO)
                try {
                    val socket = aSocket(selectorManager)
                        .udp()
                        .bind(
                            InetSocketAddress("0.0.0.0", SEARCHING_PORT),
                            configure = {
                                this.reuseAddress = true
                                this.broadcast = true
                            })
                    val receivingChannel = socket.incoming

                    var nextId = 0
                    log.i { "Starting to receive broadcast..." }
                    while (isActive) {
                        val result = receivingChannel.receiveCatching()
                        val datagram = result.getOrNull()!!
                        val message = datagram.packet.readText()
                        val address = datagram.address as InetSocketAddress
                        // "Hostname:$serverHost;Gameport:$gamePort"
                        val split = message.split(';')
                        if (split.size == 2) {
                            val hostNameFormat = split[0].split(':')
                            val gamePortFormat = split[1].split(':')
                            if (hostNameFormat[0] == "Hostname" && gamePortFormat[0] == "Gameport"
                                && hostNameFormat.size == 2 && gamePortFormat.size == 2
                            ) {
                                val lobbyName = hostNameFormat[1]
                                val gamePort: Int? = gamePortFormat[1].toIntOrNull()
                                if (gamePort != null) {
                                    val newLobby = Lobby(lobbyName, address.hostname, gamePort)
                                    if (foundLobbies.add(newLobby)) {
                                        lobbyNameFlow.emit(Pair(nextId, lobbyName))
                                        lobbyMap.set(nextId, newLobby)
                                        nextId++
                                        log.i { "Received new lobby" }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    log.i { "Udp service cancelled" }
                } finally {
                    selectorManager.close()
                    foundLobbies.clear()
                    lobbyMap.clear()
                }
            }
        }
    }

    suspend fun removeLobby(id: Int): Boolean {
        val result = lobbyMap.remove(id)
        if (result != null) {
            foundLobbies.remove(result)
            return true
        }
        return false
    }
}