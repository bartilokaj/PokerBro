package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.blokaj.pokerbro.backend.client.GameState
import pl.blokaj.pokerbro.shared.PlayerData

class GameComponent(
    componentContext: ComponentContext,
    private val scope: CoroutineScope,
    gameStateFlow: StateFlow<GameState>,
    val onPlaceBet: (bet: Int) -> Unit,
    val onTakePot: (amount: Int) -> Unit,
    val onError: (message: String) -> Unit
): ComponentContext by componentContext {
    var currentBet = MutableValue("")
    val currentTakePot = MutableValue("")
    val playersState: StateFlow<List<PlayerData>> =
        gameStateFlow
            .map { it.players }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    val potState: StateFlow<Int> =
        gameStateFlow
            .map { it.pot }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )
    val playerDataState: StateFlow<PlayerData> =
        gameStateFlow
            .map { it.playerData }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerData(-1, "", 0)
            )
    val gameLogsState: StateFlow<List<String>> =
        gameStateFlow
            .map { it.gameLogs }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

}