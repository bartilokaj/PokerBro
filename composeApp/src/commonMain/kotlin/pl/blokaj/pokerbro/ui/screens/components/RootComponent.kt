package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext


class RootComponent(
    componentContext: ComponentContext
): ComponentContext by componentContext {

}

sealed class Stage {
    object Flow: Stage()
    object FindingLobby: Stage()
    object StartingServer: Stage()
    object Game: Stage()
}