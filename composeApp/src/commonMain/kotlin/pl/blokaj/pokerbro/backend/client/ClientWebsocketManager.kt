package pl.blokaj.pokerbro.backend.client

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import okio.IOException
import pl.blokaj.pokerbro.shared.Lobby
import pl.blokaj.pokerbro.shared.getEvent

object ClientWebsocketManager {
    private val log = Logger.withTag("ClientWebSocket")
    private lateinit var eventManager: ClientEventManager

    @OptIn(ExperimentalSerializationApi::class)
    fun CoroutineScope.connectToGameServer(
        connectionStateFlow: MutableStateFlow<ConnectionState>,
        playerName: String,
        lobby: Lobby,
        onError: (message: String, lobby: Lobby) -> Unit
    ): Job {
        return launch {
            val client = HttpClient(CIO) {
                install(WebSockets)
                engine {
                    requestTimeout = 5000
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
                    eventManager = ClientEventManager(
                        this,
                        playerName,
                        onGameStart = {
                            connectionStateFlow.value = ConnectionState.GAME
                        },
                        onError = { message ->
                            onError(message, lobby)
                        }
                    )
                    connectionStateFlow.value = ConnectionState.CONNECTED
                    log.i { "Connected to ${lobby.ip}:${lobby.port}" }
                    try {
                        eventManager.register()
                        for (frame in incoming) {
                            eventManager.handleEvent(frame.getEvent())
                        }
                    } catch (e: ClosedSendChannelException) {
                        onError("Lobby ${lobby.lobbyName} closed connection", lobby)
                        log.w(e) { "Server closed connection" }
                    }
                    connectionStateFlow.value = ConnectionState.DISCONNECTED

                }
            } catch (e: Exception) {
                connectionStateFlow.value = ConnectionState.FAILED
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
            } finally {
                client.close()
            }
        }
    }

    fun getGameStateFlow(): StateFlow<GameState> {
        return eventManager.gameStateFlow
    }

    suspend fun placeBet(bet: Int) {
        eventManager.placeBet(bet)
    }

    suspend fun takePot(amount: Int) {
        eventManager.takePot(amount)
    }
}