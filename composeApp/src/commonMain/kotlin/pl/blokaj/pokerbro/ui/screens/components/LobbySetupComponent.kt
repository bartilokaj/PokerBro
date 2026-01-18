package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import pl.blokaj.pokerbro.ui.items.components.ProfilePictureComponent
import pl.blokaj.pokerbro.ui.services.interfaces.ProfilePicturePicker
import kotlin.String

class LobbySetupComponent (
    componentContext: ComponentContext,
    profilePicturePicker: ProfilePicturePicker,
    private val initialPlayerName: Value<String>,
    private val initialLobbyName: Value<String>,
    private val initialStartingFunds: Value<Int>,
    val onHostingStart: (String, String, Int) -> Unit,
    val onWrongInput: (message: String) -> Unit

) : ComponentContext by componentContext {
    val playerName = MutableValue<String>(initialPlayerName.value)
    val lobbyName = MutableValue<String>(initialLobbyName.value)
    val startingFunds = MutableValue<String>(initialStartingFunds.value.toString())
    val profilePictureComponent = ProfilePictureComponent(
        componentContext = childContext("profile picture"),
        setPath = {}
    )
}