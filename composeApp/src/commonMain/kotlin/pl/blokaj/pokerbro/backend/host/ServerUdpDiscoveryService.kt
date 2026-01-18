package pl.blokaj.pokerbro.backend.host

import co.touchlab.kermit.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

private const val UDP_PORT = 57286

object ServerUdpDiscoveryService {
    private val log = Logger.withTag("ServerUdpService")
    fun CoroutineScope.broadcastLobby(
        lanNetworkManager: LanNetworkManager,
        lobbyPort: Int,
        lobbyName: String,
        host: String
    ): Job {
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
                    val message = "LobbyName:$lobbyName;Host:$host;Port:$lobbyPort"
                    var counter = 0

                    log.i { "Broadcast starting..." }
                    while (isActive) {
                        for (i in 0..<ipAddresses.size) {
                            val packet = buildPacket({ writeText(message) })
                            val datagram = Datagram(packet, broadcastAddresses[i])
                            channels[i].send(datagram)
                        }
                        counter += 1
                        if (counter % 5 == 1) log.i { "Sent $counter broadcasts" }
                        delay(5.seconds)
                    }
                } catch (e: CancellationException) {
                    log.i(e) {"Service cancelled"}
                    throw e
                } finally {
                    selectorManager.close()
                }

            }
        }
    }
}