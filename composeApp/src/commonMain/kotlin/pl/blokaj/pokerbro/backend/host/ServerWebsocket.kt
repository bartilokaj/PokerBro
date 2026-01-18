package pl.blokaj.pokerbro.backend.host


import co.touchlab.kermit.Logger
import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.origin
import io.ktor.server.routing.routing
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.shared.getEvent
import kotlin.time.Duration.Companion.seconds


class ServerWebsocket(
    private val scope: CoroutineScope,
    private val gamePort: Int,
) {
    private val logger = Logger.withTag("GameServer")
    private val eventBus = EventBus()
    private val sessionsManager: SessionsManager = SessionsManager(scope, eventBus)
    private lateinit var eventManager: ServerEventManager

    fun start(startingFunds: Int) {
        embeddedServer(
            CIO,
            port = gamePort,
            host = "0.0.0.0"
        ) {
            module(eventBus)
        }.start(wait = false)
        eventManager = ServerEventManager(
            sessionsManager,
            eventBus,
            startingFunds
        )
        logger.i { "GameServer started on port $gamePort" }
    }

    private fun Application.module(bus: EventBus) {
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
                        eventManager.handleEvent(this, event, sessionIp)
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
                } finally {
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

    fun getPlayersFlow(): MutableStateFlow<List<PlayerData>> {
        return eventManager.players
    }
}




