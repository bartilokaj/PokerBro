package pl.blokaj.pokerbro.backend.client

import co.touchlab.kermit.Logger
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.blokaj.pokerbro.shared.Event
import pl.blokaj.pokerbro.shared.EventId
import pl.blokaj.pokerbro.shared.EventPayload.*
import pl.blokaj.pokerbro.shared.PlayerData

class ClientEventManager(
    private val session: DefaultWebSocketSession,
    playerName: String,
    private val onGameStart: () -> Unit,
    private val onError: (message: String) -> Unit
) {
    private val log = Logger.withTag("ClientEventManager")
    private val _gameStateFlow = MutableStateFlow(
        GameState(
            players = emptyList(),
            playerData = PlayerData(
                id = -1,
                name = playerName,
                funds = -1,
            ),
            pot = 0,
            gameLogs = emptyList()
        )
    )
    val gameStateFlow: StateFlow<GameState> = _gameStateFlow.asStateFlow()
    private val eventCounter = atomic(1)

    fun handleEvent(event: Event) {
        log.i { "Received $event" }
        when (val payload = event.payload) {
            is RegistrationAccepted -> {
                log.i { "Received id ${payload.playerId}" }
                _gameStateFlow.update { state ->
                    state.copy(
                        playerData = state.playerData.copy(
                            id = payload.playerId
                        )
                    )
                }
            }
            is RegisterOtherPlayers -> {
                log.i { "Received other players $payload" }
                _gameStateFlow.update { state ->
                    state.copy(
                        players = payload.players.toList()
                    )
                }
                log.i { "All players: ${_gameStateFlow.value.players}" }
            }
            is Start -> {
                log.i { "Received start message" }
                _gameStateFlow.update { state ->
                    state.copy(
                        gameLogs = state.gameLogs + "Game starts",
                        playerData = state.playerData.copy(funds = payload.startingFunds)
                    )
                }
                onGameStart()
            }
            is AcceptedBet -> {
                val playerId = payload.playerId
                _gameStateFlow.update { state ->
                    var logMessage: String? = null
                    state.copy(
                        players = state.players.map { playerData ->
                            if (playerData.id == playerId) {
                                logMessage = "${playerData.name} (id:$playerId) bets ${payload.bet}"
                                playerData.copy(funds = playerData.funds - payload.bet)
                            } else playerData
                        },
                        playerData =
                            if (playerId == state.playerData.id) {
                                state.playerData.copy(funds = state.playerData.funds - payload.bet)
                            } else state.playerData,
                        pot = payload.potAfter,
                        gameLogs =
                            if (logMessage != null) {
                                state.gameLogs + logMessage!!
                            } else {
                                state.gameLogs
                            }

                    )
                }
            }
            is PotTaken -> {
                val playerId = event.payload.playerId
                var logMessage: String? = null
                _gameStateFlow.update { state ->
                    state.copy(
                        players = state.players.map { playerData ->
                            if (playerData.id == playerId) {
                                logMessage = "${playerData.name} (id: ${playerData.id}) takes ${payload.amount} from the pot"
                                playerData.copy(funds = playerData.funds + payload.amount)
                            } else playerData
                        },
                        playerData =
                            if (playerId == state.playerData.id) {
                                state.playerData.copy(funds = state.playerData.funds + payload.amount)
                            } else state.playerData,
                        pot = payload.potAfter,
                        gameLogs =
                            if (logMessage != null) {
                                state.gameLogs + logMessage!!
                            } else {
                                state.gameLogs
                            }
                    )
                }
            }
            is Warning -> {
                log.i { "Received warning $payload" }
            }
            is Register,
            is PlaceBet,
            is TakePot -> Unit
        }
    }

    suspend fun register() {
        val register = Event(
            EventId(0),
            Register(_gameStateFlow.value.playerData.name)
        )
        session.send(register.generateEventFrame())
    }

    suspend fun placeBet(bet: Int) {
        if (bet > _gameStateFlow.value.playerData.funds) {
            onError("Can't place bet above your funds")
        } else {
            val placeBet = Event(
                EventId.newEventId(_gameStateFlow.value.playerData.id, eventCounter.getAndIncrement()),
                PlaceBet(bet)
            )
            session.send(placeBet.generateEventFrame())
        }
    }

    suspend fun takePot(amount: Int) {
        if (_gameStateFlow.value.pot < amount) {
            onError("Can't take more then is in the pot")
        } else {
            val takePot = Event(
                EventId.newEventId(_gameStateFlow.value.playerData.id, eventCounter.getAndIncrement()),
                TakePot(amount)
            )
            session.send(takePot.generateEventFrame())
        }
    }
}