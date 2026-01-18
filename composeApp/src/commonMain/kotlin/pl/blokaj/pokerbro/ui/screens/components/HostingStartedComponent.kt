package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import kotlinx.coroutines.flow.MutableStateFlow
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent

class HostingStartedComponent(
    componentContext: ComponentContext,
    players: MutableStateFlow<List<PlayerData>>
): ComponentContext by componentContext {
    val listComponent = FlowListComponent(
        childContext("Players list"),
        players,
        onElementClicked = {
        },
        toStringFn = { player ->
            player.name
        }
    )
}