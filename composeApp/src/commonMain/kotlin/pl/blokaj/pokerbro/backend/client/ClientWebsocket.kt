package pl.blokaj.pokerbro.backend.client

import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.FailToConnectException
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.websocket.WebSocketException
import kotlinx.coroutines.CoroutineScope
import pl.blokaj.pokerbro.shared.Lobby
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.utility.ThreadSafeSet
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.content.EntityTagVersion
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.Frame
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import okio.IOException
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.EventPayload
import pl.blokaj.pokerbro.utility.ThreadSafeMap

object ClientWebsocket {
    private val log = Logger.withTag("ClientWebSocket")

    @OptIn(ExperimentalSerializationApi::class)
    fun CoroutineScope.connectToGameServer(
        lobby: Lobby,
        connectionStateFlow: MutableStateFlow<ConnectionState>,
        playerData: PlayerData,
        players: MutableStateFlow<LinkedHashMap<Int, PlayerData>>,
        onError: (message: String, lobby: Lobby) -> Unit
    ): Job {
        return launch {
            val client = HttpClient(CIO) {
                install(WebSockets)
                engine {
                    requestTimeout = 5000      // 5 seconds
                    endpoint {
                        connectTimeout = 5000  // connection timeout
                        keepAliveTime = 5000
                    }
                }
            }
            connectionStateFlow.value = ConnectionState.CONNECTING
            log.i { "Connecting to ${lobby.ip}:${lobby.port}" }
            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = lobby.ip,
                    port = lobby.port,
                    path = "/game"
                ) {
                    connectionStateFlow.value = ConnectionState.CONNECTED
                    val eventManager = ClientEventManager(
                        this,
                        playerData,
                        players,
                        onGameStart = {
                            connectionStateFlow.value = ConnectionState.GAME
                        }
                    )
                    log.i { "Connected to ${lobby.ip}:${lobby.port}" }
                    try {
                        eventManager.register()
                        for (frame in incoming) {

                        }
                    } catch (e: ClosedSendChannelException) {
                        onError("Lobby ${lobby.lobbyName} closed connection", lobby)
                        log.w(e) { "Server closed connection" }
                    }
                    connectionStateFlow.value = ConnectionState.DISCONNECTED

                }
            } catch (e: Exception) {
                onError("Failed to connect to server", lobby)
                when (e) {
                    is CancellationException -> throw e
                    is UnresolvedAddressException -> log.e(e) { "Invalid IP: ${lobby.ip}" }
                    is ConnectTimeoutException -> log.e(e) { "Server not responding" }
                    is FailToConnectException -> log.e(e) { "Server refused connection" }
                    is WebSocketException -> log.e(e) { "WebSocket protocol error" }
                    is IOException -> log.e(e) { "Network error" }
                    else -> log.e(e) { "Unexpected exception" }
                }
                connectionStateFlow.value = ConnectionState.FAILED
            } finally {
                client.close()
            }
        }
    }
}