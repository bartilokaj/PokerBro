package pl.blokaj.pokerbro.backend.host

import co.touchlab.kermit.Logger
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
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
    private val _players = MutableStateFlow<List<PlayerData>>(emptyList())
    val players: StateFlow<List<PlayerData>> get() = _players.asStateFlow()
    private val pot = atomic(0)
    private val log = Logger.withTag("ServerEventManager")
    private val ipToPlayerDataMap = ThreadSafeMap<String, PlayerData>(HashMap())

    suspend fun handleEvent(
        session: DefaultWebSocketSession,
        event: Event,
        sessionIp: String,
        lastEventRead: EventId
    ) {
        log.i { "Received ${event.payload} from ${event.eventId.playerId}" }
        when (val payload = event.payload) {
            is Register -> {
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
                    sessionsManager.addConnection(session, playerId)
                    val newPlayer = PlayerData(playerId, payload.playerName, startingFunds)
                    log.i { "Registered new player $newPlayer" }
                    _players.update {
                        it + newPlayer
                    }
                }
                ipToPlayerDataMap.set(sessionIp, PlayerData(playerId, payload.playerName, startingFunds))
                sessionsManager.sendSingleFrame(session, response.generateEventFrame())
            }
            is PlaceBet -> {
                val playerId = event.eventId.playerId
                var success = false
                _players.update { playerList ->
                    playerList.map { player ->
                        if (playerId == player.id && player.funds >= payload.bet) {
                            success = true
                            player.copy(funds = player.funds - payload.bet)
                        } else {
                            player
                        }
                    }
                }
                if (success) {
                    val potAfter = pot.addAndGet(payload.bet)
                    eventBus.produceEvent(Event(
                        EventId.newEventId(0, eventCounter.getAndIncrement()),
                        AcceptedBet(
                            playerId,
                            payload.bet,
                            potAfter
                        )
                    ))
                } else {
                    log.i { "Sending warning to ${event.eventId.playerId}: Can't bet more then you own" }
                    sendWarning(session, "Can't bet more then you own", lastEventRead)
                }

            }
            is TakePot -> {
                val playerId = event.eventId.playerId
                var takePotSuccess = false
                val potAfter = pot.updateAndGet { current ->
                    if (current >= payload.amount) {
                        takePotSuccess = true
                        current - payload.amount
                    }
                    else current
                }
                var updatePlayerSuccess = false
                if (takePotSuccess) {
                    _players.update { playerList ->
                        playerList.map { player ->
                            if (playerId == player.id) {
                                updatePlayerSuccess = true
                                player.copy(funds = player.funds + payload.amount)
                            } else {
                                player
                            }
                        }
                    }
                } else {
                    log.i { "Sending warning to ${event.eventId.playerId}: Can't take more than is in the pot" }
                    sendWarning(session, "Can't take more than is in the pot", lastEventRead)
                }
                if (updatePlayerSuccess) {
                    eventBus.produceEvent(Event(
                        EventId.newEventId(0, eventCounter.getAndIncrement()),
                        PotTaken(
                            playerId,
                            payload.amount,
                            potAfter)
                    ))
                } else {
                    log.i { "Sending warning to ${event.eventId.playerId}: Wrong playerId" }
                    sendWarning(session, "Wrong playerId", lastEventRead)
                }
            }
            is RegisterOtherPlayers,
            is RegistrationAccepted,
            is Warning,
            is AcceptedBet,
            is PotTaken,
            is Start -> Unit
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
            _players.update {
                val player = it.firstOrNull { p -> p.id == playerId }
                if (player != null) it - player else it
            }
            log.i { "Removed $sessionIp from player list" }
        }
    }
}