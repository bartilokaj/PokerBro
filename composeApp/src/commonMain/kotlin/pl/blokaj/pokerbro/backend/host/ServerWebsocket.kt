package pl.blokaj.pokerbro.backend.host


import co.touchlab.kermit.Logger
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.shared.getEvent
import kotlin.time.Duration.Companion.seconds


class ServerWebsocket(
    private val scope: CoroutineScope,
    private val gamePort: Int,
    private val onFailure: () -> Unit
) {
    private val logger = Logger.withTag("GameServer")
    private val eventBus = EventBus()
    private val sessionsManager: SessionsManager = SessionsManager(scope, eventBus)
    private lateinit var eventManager: ServerEventManager
    private lateinit var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>

    fun start(
        serverState: MutableStateFlow<ServerState>,
        startingFunds: Int
    ) {
        eventManager = ServerEventManager(
            sessionsManager,
            eventBus,
            startingFunds
        )
        server = embeddedServer(
            CIO,
            port = gamePort,
            host = "0.0.0.0"
        ) {
            module()
        }.start(wait = false)
        logger.i { "GameServer started on port $gamePort with starting funds $startingFunds" }
        serverState.value = ServerState.RUNNING
    }

    fun stop() {
        if (::server.isInitialized) {
            server.stop(gracePeriodMillis = 5000)
            logger.i { "Game server stopped" }
        }
    }

    private fun Application.module() {
        install(WebSockets) {
            pingPeriod = 15.seconds      // send ping every 15s
            timeout = 30.seconds
        }

        routing {
            webSocket("/game") {
                val sessionIp = call.request.origin.remoteHost
                val sessionPort = call.request.origin.remotePort
                logger.i { "Started new connection with $sessionIp:$sessionPort" }
                var lastEventId = EventId(0)
                try {
                    for (frame in incoming) {
                        val event = frame.getEvent()
                        lastEventId = event.eventId
                        eventManager.handleEvent(this, event, sessionIp, lastEventId)
                    }
                } catch (e: CancellationException) {
                    logger.i(e) { "Client $sessionIp:$sessionPort ended session" }
                    throw e
                } catch (e: SerializationException) {
                    logger.w(e) { "Bad format from client $sessionIp:$sessionPort" }
                    eventManager.sendWarning(
                        this,
                        "Bad format from client",
                        lastEventId
                    )
                } catch (e: Exception) {
                    logger.e(e) { "WebSocket failure" }
                    onFailure()
                } finally {
                    logger.i { "Ending session with $sessionIp:$sessionPort" }
                    eventManager.removePlayerData(sessionIp)
                }
            }
        }
    }

    fun startGame() {
        scope.launch {
            eventManager.startGame()
        }
    }

    fun getPlayersFlow(): StateFlow<List<PlayerData>> {
        return eventManager.players
    }
}




