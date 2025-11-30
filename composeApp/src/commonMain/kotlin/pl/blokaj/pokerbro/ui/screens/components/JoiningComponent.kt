package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import io.ktor.http.ContentDisposition.Companion.File
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
    profilePicturePicker: ProfilePicturePicker,
    initialName: String,
    var onLobbySearch: (String, String) -> Unit
) : ComponentContext by componentContext {
    private var _playerName = MutableValue<String>(initialName)

    val playerName: Value<String> get() = _playerName
    val profilePictureComponent = ProfilePictureComponent(
        componentContext = childContext("profile picture"),
        setPath = {}
    )

    fun setPlayerName(name: String) {
        _playerName.value = name
    }

    fun updatePlayerDetails() {
        val imageString = profilePictureComponent.getImageString()
        onLobbySearch(_playerName.value, imageString)
    }
}