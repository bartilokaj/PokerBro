package pl.blokaj.pokerbro.backend.host

import co.touchlab.kermit.Logger
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import pl.blokaj.pokerbro.backend.LanNetworkManager
import kotlin.time.Duration.Companion.seconds

private const val UDP_PORT = 57286

object ServerUdpDiscoveryService {
    private val log = Logger.withTag("ServerUdpService")
    fun CoroutineScope.broadcastLobby(
        lanNetworkManager: LanNetworkManager,
        lobbyPort: Int,
        lobbyName: String,
        host: String,
        onFailure: () -> Unit
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
                            .bind(InetSocketAddress(ip, 0), configure = { this.broadcast = true })
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
                } catch (e: Exception) {
                    log.e(e) { "Unexpected exception" }
                    onFailure()
                } finally {
                    selectorManager.close()
                }

            }
        }
    }
}