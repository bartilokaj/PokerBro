package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.client.ClientConnectionManager
import pl.blokaj.pokerbro.shared.Lobby
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent

class LobbySearchComponent(
    componentContext: ComponentContext,
    private val clientConnectionManager: ClientConnectionManager,
    private val onLobbyFound: (Pair<Int, String>) -> Unit
): ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _selectedLobby = MutableStateFlow<Pair<Int, String>?>(null)
    val selectedLobby: StateFlow<Pair<Int, String>?> get() = _selectedLobby.asStateFlow()

    fun onLobbyConfirmed(lobbyPair: Pair<Int, String>) {
        onLobbyFound(lobbyPair)
    }

    fun onLobbyDismissed() {
        _selectedLobby.value = null
    }

    val listComponent = FlowListComponent<Pair<Int, String>>(
        componentContext = childContext("lobby list"),
        flow = clientConnectionManager.getLobbyFlow(),
        scope,
        onElementClicked =  { element ->
            _selectedLobby.value = element
        },
        toStringFn = { it.component2() }
    )
}