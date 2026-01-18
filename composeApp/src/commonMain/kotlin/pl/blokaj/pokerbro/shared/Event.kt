package pl.blokaj.pokerbro.shared

import io.ktor.websocket.CloseReason
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

private const val eventCounterMask = 0x3FFFFF
private const val playerIdMask = eventCounterMask.inv()
private const val playerIdLength = 10
private const val eventCounterLength = 22

@Serializable
sealed interface EventPayload {
    @Serializable
    data class Register(val playerName: String): EventPayload
    @Serializable
    data class RegisterPlayers(val players: PlayerData): EventPayload
    @Serializable
    data class Warning(val reason: String, val lastEventRead: EventId): EventPayload
    @Serializable
    data class Start(val startingFunds: Int): EventPayload
}

@Serializable
data class Event(
    val eventId: EventId,
    val payload: EventPayload
)

@Serializable
@JvmInline
value class EventId(val id: Int) {
    val playerId: Int
        get() = (id and playerIdMask) ushr playerIdLength
    val eventCounter: Int
        get() = (id and eventCounterMask)

    companion object {
        fun newEventId(playerId: Int, eventCounter: Int): EventId {
            require(playerId in 0..1023) { "player id too large" }
            require(eventCounter in 0..0x3FFFFF) { "counter too large" }
            return EventId((playerId shl eventCounterLength) or (eventCounter and eventCounterMask))
        }
    }
}