package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.blokaj.pokerbro.backend.client.ClientConnectionManager
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent

class LobbySearchComponent(
    componentContext: ComponentContext,
    clientConnectionManager: ClientConnectionManager,
    private val onLobbyFound: (Pair<Int, String>) -> Unit,
    val onBack: () -> Unit
): ComponentContext by componentContext {
    private val _selectedLobby = MutableStateFlow<Pair<Int, String>?>(null)
    val selectedLobby: StateFlow<Pair<Int, String>?> get() = _selectedLobby.asStateFlow()

    fun onLobbyConfirmed(lobbyPair: Pair<Int, String>) {
        onLobbyFound(lobbyPair)
        _selectedLobby.value = null
    }

    fun onLobbyDismissed() {
        _selectedLobby.value = null
    }

    val listComponent = FlowListComponent(
        componentContext = childContext("lobby list"),
        flow = clientConnectionManager.getLobbyFlow(),
        "Found Lobbies",
        onElementClicked =  { element ->
            _selectedLobby.value = element
        },
        toStringFn = { it.component2() }
    )
}