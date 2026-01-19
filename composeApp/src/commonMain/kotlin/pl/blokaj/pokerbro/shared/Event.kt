package pl.blokaj.pokerbro.shared

import io.ktor.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.jvm.JvmInline

private const val eventCounterMask = 0x3FFFFF
private const val playerIdMask = eventCounterMask.inv()
private const val playerIdLength = 10
private const val eventCounterLength = 22

/**
 * Sealed interface for websocket communication.
 */
@Serializable
sealed interface EventPayload {
    /**
     * Sender: Player
     *
     * Registering in the lobby, expecting answer with player id.
     * Must be sent before sending any other event.
     */
    @Serializable
    data class Register(val playerName: String): EventPayload

    /**
     * Sender: Server
     *
     * Acceptance of registration of a new player
     */
    @Serializable
    data class RegistrationAccepted(val playerId: Int): EventPayload

    /**
     * Sender: Server
     *
     * Details about every player (receiver included).
     * Send at the start of the game to every player (but before Start payload).
     */
    @Serializable
    data class RegisterOtherPlayers(val players: List<PlayerData>): EventPayload

    /**
     * Sender: Server
     *
     * Warning to the player.
     * Mostly for debugging, currently server acknowledges wrong message,
     * sends this payload and ignores.
     * Payload includes last event id read.
     */
    @Serializable
    data class Warning(val reason: String, val lastEventRead: EventId): EventPayload

    /**
     * Sender: Server
     *
     * Starts the game.
     */
    @Serializable
    data class Start(val startingFunds: Int): EventPayload

    @Serializable
    data class PlaceBet(val bet: Int): EventPayload

    @Serializable
    data class TakePot(val amount: Int): EventPayload

    @Serializable
    data class AcceptedBet(val playerId: Int, val bet: Int, val potAfter: Int): EventPayload

    @Serializable
    data class PotTaken(val playerId: Int, val amount: Int, val potAfter: Int): EventPayload
}

@Serializable
data class Event(
    val eventId: EventId,
    val payload: EventPayload
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun generateEventFrame(): Frame.Binary {
        return Frame.Binary(
            fin = true,
            Cbor.encodeToByteArray(
                serializer(),
                this
            )
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Frame.getEvent(): Event {
    return Cbor.decodeFromByteArray<Event>(
        this.data
    )
}

/**
 * ID that must be included in every event.
 * It is 32-bit integer, whose first 10 bits are player id
 * and the remaining 22 are event counter.
 * Server's ID is always 0.
 */
@Serializable
@JvmInline
value class EventId(val id: Int) {
    val playerId: Int
        get() = (id and playerIdMask) ushr eventCounterLength
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