package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.blokaj.pokerbro.backend.host.ServerState
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent

class HostingStartedComponent(
    componentContext: ComponentContext,
    val serverState: MutableStateFlow<ServerState>,
    players: StateFlow<List<PlayerData>>,
    val onGameStart: () -> Unit,
    val onBack: () -> Unit,
    val onError: (message: String) -> Unit
): ComponentContext by componentContext {
    val listComponent = FlowListComponent(
        childContext("Players list"),
        players,
        "Players",
        onElementClicked = {
        },
        toStringFn = { player ->
            player.name
        }
    )
}