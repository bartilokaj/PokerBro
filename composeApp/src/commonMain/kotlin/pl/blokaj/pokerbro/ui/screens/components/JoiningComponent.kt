package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.client.ktor.Lobby
import pl.blokaj.pokerbro.backend.client.ktor.startGameSearching
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent
import pl.blokaj.pokerbro.ui.items.components.ProfilePictureComponent
import pl.blokaj.pokerbro.ui.services.interfaces.ProfilePicturePicker

class JoiningComponent (
    componentContext: ComponentContext,
    profilePicturePicker: ProfilePicturePicker
) : ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val lobbyFlow = MutableSharedFlow<Lobby>()
    private var _playerName = MutableValue<String>("")

    val playerName: Value<String> get() = _playerName
    val lobbyComponent = FlowListComponent<Lobby>(componentContext, lobbyFlow, {})
    val profilePictureComponent = ProfilePictureComponent(componentContext, {})

    fun setPlayerName(name: String) {
        _playerName.value = name
    }

    fun startSearchingForLobby() {
        println("Starting searching for lobbies")
        scope.launch {
            startGameSearching("barti", lobbyFlow)
        }
    }



}