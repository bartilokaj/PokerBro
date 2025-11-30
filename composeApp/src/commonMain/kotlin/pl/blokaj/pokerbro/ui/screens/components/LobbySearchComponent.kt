package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.client.ktor.Lobby
import pl.blokaj.pokerbro.backend.client.ktor.startGameSearching
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent
import pl.blokaj.pokerbro.ui.items.interfaces.ListComponent

class LobbySearchComponent(
    componentContext: ComponentContext
): ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val lobbyFlow = MutableSharedFlow<Lobby>(replay = 100)
    private lateinit var searchingJob: Job

    init {
        scope.launch {
            searchingJob = startGameSearching(lobbyFlow)
            lobbyFlow.emit(Lobby("dummy", "6.7.6.7", 4200))
        }
    }

    val listComponent = FlowListComponent<Lobby>(
        componentContext = childContext("lobby list"),
        flow = lobbyFlow,
        elementClicked = { println(it.toString()) }
    )
}