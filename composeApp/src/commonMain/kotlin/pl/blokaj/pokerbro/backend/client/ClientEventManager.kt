package pl.blokaj.pokerbro.backend.client

import co.touchlab.kermit.Logger
import io.ktor.websocket.DefaultWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.EventPayload
import pl.blokaj.pokerbro.shared.PlayerData

class ClientEventManager(
    private val session: DefaultWebSocketSession,
    private val playerData: PlayerData,
    private val players: MutableStateFlow<LinkedHashMap<Int, PlayerData>>,
    private val onGameStart: () -> Unit
) {
    private val log = Logger.withTag("ClientEventManager")

    fun handleEvent(event: Event) {
        when (val payload = event.payload) {
            is EventPayload.RegistrationAccepted -> {
                playerData.id = payload.playerId
            }
            is EventPayload.RegisterOtherPlayers -> {
                players.update {
                    payload.players.associateByTo(LinkedHashMap()) { it.id }
                }
            }
            is EventPayload.Start -> onGameStart()
            is EventPayload.Warning -> {
                log.i { "Received warning $payload" }
            }
            is EventPayload.Register -> {
                // events ignored
            }
        }
    }

    suspend fun register() {
        val register = Event(
            EventId(0),
            EventPayload.Register(playerData.name)
        )
        session.send(register.generateEventFrame())
    }
}