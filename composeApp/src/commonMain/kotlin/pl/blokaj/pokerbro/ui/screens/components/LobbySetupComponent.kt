package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import pl.blokaj.pokerbro.ui.items.components.ProfilePictureComponent
import pl.blokaj.pokerbro.ui.services.interfaces.ProfilePicturePicker

class LobbySetupComponent (
    componentContext: ComponentContext,
    profilePicturePicker: ProfilePicturePicker,
    initialPlayerName: Value<String>,
    initialLobbyName: Value<String>,
    initialStartingFunds: Value<Int>,
    val onHostingStart: (String, String, Int) -> Unit,
    val onWrongInput: (message: String) -> Unit

) : ComponentContext by componentContext {
    val playerName = MutableValue(initialPlayerName.value)
    val lobbyName = MutableValue(initialLobbyName.value)
    val startingFunds = MutableValue(initialStartingFunds.value.toString())
    val profilePictureComponent = ProfilePictureComponent(
        componentContext = childContext("profile picture"),
        setPath = {}
    )
}