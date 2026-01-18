package pl.blokaj.pokerbro.backend.client

import co.touchlab.kermit.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
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
import pl.blokaj.pokerbro.shared.Lobby
import pl.blokaj.pokerbro.utility.ThreadSafeMap
import pl.blokaj.pokerbro.utility.ThreadSafeSet

private const val SEARCHING_PORT = 57286

object ClientUdpDiscoveryService {
    private val log = Logger.withTag("ClientUdpService")
    private val foundLobbies = ThreadSafeSet<Lobby>(HashSet())

    fun CoroutineScope.startGameSearching(
        lanNetworkManager: LanNetworkManager,
        lobbyMap: ThreadSafeMap<Int, Lobby>,
        lobbyNameFlow: MutableSharedFlow<Pair<Int, String>>
    ): Job {
        return launch {
            lanNetworkManager.withBroadcastLock {
                val selectorManager = SelectorManager(Dispatchers.IO)
                var socket: BoundDatagramSocket? = null
                try {
                    socket = aSocket(selectorManager)
                        .udp()
                        .bind(
                            InetSocketAddress("0.0.0.0", SEARCHING_PORT),
                            configure = {
                                this.reuseAddress = true
                                this.broadcast = true
                            })
                    val receivingChannel = socket.incoming

                    var nextId = 0
                    log.i { "Starting to listen..." }
                    while (isActive) {
                        val result = receivingChannel.receiveCatching()
                        val datagram = result.getOrNull()!!
                        val message = datagram.packet.readText()
                        val address = datagram.address as InetSocketAddress
                        if (message.length < 255) {
                            log.i { "Received: $message" }
                        }
                        // "LobbyName:$lobbyName;Host:$host;Port:$lobbyPort"
                        val split = message.split(';')
                        if (split.size == 3) {
                            val lobbyNameSplit = split[0].split(':')
                            val hostNameSplit = split[1].split(':')
                            val gamePortSplit = split[2].split(':')

                            if (lobbyNameSplit[0] == "LobbyName" && lobbyNameSplit.size == 2 &&
                                hostNameSplit[0] == "Host" && hostNameSplit.size == 2 &&
                                gamePortSplit[0] == "Port" && gamePortSplit.size == 2
                            ) {
                                val lobbyName = lobbyNameSplit[1]
                                val hostName = hostNameSplit[1]
                                val gamePort: Int? = gamePortSplit[1].toIntOrNull()
                                if (gamePort != null) {
                                    val newLobby = Lobby(
                                        lobbyName,
                                        hostName,
                                        address.hostname,
                                        gamePort
                                    )
                                    if (foundLobbies.add(newLobby)) {
                                        lobbyNameFlow.emit(Pair(nextId, "$lobbyName by $hostName"))
                                        lobbyMap.set(nextId, newLobby)
                                        nextId++
                                        log.i { "Received new lobby" }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    log.i(e) { "Udp service cancelled" }
                    throw e
                } catch(e: Exception) {
                    log.e(e) { "Unexpected exception" }
                } finally {
                    socket?.close()
                    selectorManager.close()
                    foundLobbies.clear()
                    lobbyMap.clear()
                }
            }
        }
    }

    suspend fun removeLobby(lobby: Lobby): Boolean {
        return foundLobbies.remove(lobby)
    }
}