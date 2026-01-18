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
    private val connectionMap: ThreadSafeMap<Int, Job> = ThreadSafeMap(HashMap())
    private val log = withTag("ConnectionManager")

    suspend fun addConnection(session: DefaultWebSocketSession, playerId: Int) {
        val newJob = scope.launch {
            try {
                eventBus.events.collect { event ->
                    val jsonString = Json.encodeToString<Event>(event)
                    val frame = Frame.Text(jsonString)
                    try {
                        session.send(frame)
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
            } catch (e: Exception) {
                log.e(e) { "Unexpected exception" }
            }
        }
        connectionMap.set(playerId, newJob)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun sendWarning(
        session: DefaultWebSocketSession,
        reason: String,
        lastEventRead: EventId,
        eventId: EventId
    ) {
        scope.launch {
            try {
                val warningEvent = Event(eventId, EventPayload.Warning(reason, lastEventRead))
                val byteArray = Cbor.encodeToByteArray(
                    Event.serializer(),
                    warningEvent
                )
                session.send(Frame.Binary(true, byteArray))
            } catch (e: ClosedSendChannelException) {
                log.e(e) { "Session closed (will be removed next time something will be send)" }
            } catch (e: IOException) {
                log.e(e) { "Network error sending to session" }
            }
        }
    }
}