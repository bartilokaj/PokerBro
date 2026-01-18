package pl.blokaj.pokerbro.backend.host

import co.touchlab.kermit.Logger.Companion.withTag
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.EventPayload
import pl.blokaj.pokerbro.utility.ThreadSafeMap
import kotlinx.serialization.cbor.Cbor

class SessionsManager(
    private val scope: CoroutineScope,
    private val eventBus: EventBus
) {
    // many coroutines can access
    private val connectionMap: ThreadSafeMap<Int, Job> = ThreadSafeMap(LinkedHashMap())
    private val log = withTag("ConnectionManager")
    private val ipToPlayerIdMap = ThreadSafeMap<String, Int>(HashMap())
    fun addConnection(session: DefaultWebSocketSession, sessionIp: String, playerId: Int) {
        val newJob = scope.launch {
            try {
                eventBus.events.collect { event ->
                    try {
                        session.send(event.generateEventFrame())
                    } catch (e: ClosedSendChannelException) {
                        log.e(e) { "Session closed, removing" }
                        connectionMap.remove(playerId)?.cancel()
                        cancel() // cancel this coroutine
                    } catch (e: IOException) {
                        log.e(e) { "Network error sending to session" }
                        connectionMap.remove(playerId)?.cancel()
                        cancel()
                    }
                }
            } catch (e: CancellationException) {
                log.i(e) { "Coroutine stopped" }
                throw e
            } catch (e: Exception) {
                log.e(e) { "Unexpected exception" }
            }
        }
        scope.launch {
            connectionMap.set(playerId, newJob)
        }
    }

    fun sendSingleFrame(session: DefaultWebSocketSession, frame: Frame) {
        scope.launch {
            try {
                session.send(frame)
            } catch (e: ClosedSendChannelException) {
                log.e(e) { "Session closed (will be removed next time something will be send)" }
            } catch (e: IOException) {
                log.e(e) { "Network error sending to session" }
            }
        }
    }
}