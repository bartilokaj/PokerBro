package pl.blokaj.pokerbro.backend.host

import co.touchlab.kermit.Logger
import io.ktor.websocket.DefaultWebSocketSession
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.EventPayload
import pl.blokaj.pokerbro.shared.EventPayload.*
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.utility.ThreadSafeMap

class ServerEventManager(
    private val sessionsManager: SessionsManager,
    private val eventBus: EventBus,
    private val startingFunds: Int
) {
    private val playerCounter = atomic(0)
    private val eventCounter = atomic(1)
    val players = MutableStateFlow<List<PlayerData>>(emptyList())
    private val log = Logger.withTag("ServerEventManager")
    private val ipToPlayerDataMap = ThreadSafeMap<String, PlayerData>(HashMap())

    suspend fun handleEvent(
        session: DefaultWebSocketSession,
        event: Event,
        sessionIp: String
    ) {
        when (val payload = event.payload) {
            is EventPayload.Register -> {
                val response: Event
                var playerId = ipToPlayerDataMap.get(sessionIp)?.id
                if (playerId != null) {
                    response = Event(
                        EventId(0),
                        RegistrationAccepted(playerId)
                    )
                } else {
                    playerId = playerCounter.incrementAndGet()
                    response = Event(
                        EventId(0),
                        RegistrationAccepted(playerId)
                    )
                    sessionsManager.addConnection(session, sessionIp, playerId)
                    players.update {
                        it + PlayerData(playerId, payload.playerName, startingFunds)
                    }
                }
                ipToPlayerDataMap.set(sessionIp, PlayerData(playerId, payload.playerName, startingFunds))
                sessionsManager.sendSingleFrame(session, response.generateEventFrame())
            }
            else -> {
                // pass
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun sendWarning(
        session: DefaultWebSocketSession,
        reason: String,
        lastEventRead: EventId
    ) {
        val warning = Event(
            EventId(eventCounter.getAndIncrement()),
            Warning(reason, lastEventRead)
        )
        sessionsManager.sendSingleFrame(session, warning.generateEventFrame())
    }

    suspend fun startGame() {
        val playerRegistration = Event(
            EventId(eventCounter.getAndIncrement()),
            RegisterOtherPlayers(players.value)
        )
        eventBus.produceEvent(playerRegistration)

        val start = Event(
            EventId(eventCounter.getAndIncrement()),
            Start(startingFunds)
        )
        eventBus.produceEvent(start)
    }

    suspend fun removePlayerData(sessionIp: String) {
        val playerId = ipToPlayerDataMap.remove(sessionIp)?.id
        if (playerId != null) {
            players.update {
                val player = it.firstOrNull { p -> p.id == playerId }
                if (player != null) it - player else it
            }
            log.i { "Removed $sessionIp from player list" }
        }
    }
}