package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import pl.blokaj.pokerbro.backend.host.ktor.startServer
import pl.blokaj.pokerbro.serverLogBus
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent
import pl.blokaj.pokerbro.ui.items.components.ProfilePictureComponent
import pl.blokaj.pokerbro.ui.services.interfaces.ProfilePicturePicker

class HostingComponent (
    componentContext: ComponentContext,
    profilePicturePicker: ProfilePicturePicker,
    initialName: String,
    var onHostingStart: (String, String) -> Unit
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
        onHostingStart(_playerName.value, imageString)
    }
}