package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import pl.blokaj.pokerbro.ui.items.components.ProfilePictureComponent
import pl.blokaj.pokerbro.ui.services.interfaces.ProfilePicturePicker

class JoiningComponent (
    componentContext: ComponentContext,
    profilePicturePicker: ProfilePicturePicker,
    private val initialPlayerName: Value<String>,
    val onLobbySearch: (playerName: String) -> Unit,
    val onWrongInput: (reason: String) -> Unit
) : ComponentContext by componentContext {
    val localName = MutableValue<String>(initialPlayerName.value)
    val profilePictureComponent = ProfilePictureComponent(
        componentContext = childContext("profile picture"),
        setPath = {}
    )
}