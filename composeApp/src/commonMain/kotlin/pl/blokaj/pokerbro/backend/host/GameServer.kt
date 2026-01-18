package pl.blokaj.pokerbro.backend.host


import co.touchlab.kermit.Logger
import io.ktor.client.request.invoke
import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.origin
import io.ktor.server.routing.routing
import io.ktor.utils.io.CancellationException
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.utility.ThreadSafeMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class GameServer(
    private val scope: CoroutineScope,
    private val gamePort: Int
) {
    private val logger = Logger.withTag("GameServer")
    private val eventBus = EventBus()
    private var sessionsManager: SessionsManager = SessionsManager(scope, eventBus)
    private val playerMap: ThreadSafeMap<String, PlayerData> = ThreadSafeMap(LinkedHashMap())
    private val eventCounter = atomic(0)

    fun start() {
        embeddedServer(
            CIO,
            port = gamePort,
            host = "0.0.0.0"
        ) {
            module(eventBus)
        }.start(wait = false)
        logger.i { "GameServer started on port $gamePort" }
    }

    private fun handleEvent() {
        TODO()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun Application.module(bus: EventBus) {
        install(WebSockets) {
            pingPeriod = 15.seconds      // send ping every 15s
            timeout = 30.seconds
        }

        routing {
            webSocket("/game") {
                logger.i { "Started new connection with ${call.request.origin.remoteHost}:${call.request.origin.remotePort}" }
                var lastEventId = EventId(0)
                try {
                    for (frame in incoming) {
                        val event = Cbor.decodeFromByteArray<Event>(frame.data)
                        lastEventId = event.eventId
                        handleEvent()
                    }
                } catch (e: CancellationException) {
                    logger.i(e) { "Client ended session" }
                } catch (e: SerializationException) {
                    logger.w(e) { "Bad format from client" }
                    sessionsManager.sendWarning(
                        this,
                        "Bad massage format",
                        lastEventId,
                        EventId(eventCounter.getAndIncrement())
                    )
                } catch (e: Exception) {
                    logger.e(e) { "WebSocket failure" }
                }
            }
        }
    }
}




