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
    val initialPlayerName: String,
    val initialLobbyName: String,
    val initialStartingFunds: Int,
    val onHostingStart: (String, String, Int) -> Unit,
    val onWrongInput: (message: String) -> Unit

) : ComponentContext by componentContext {

    val profilePictureComponent = ProfilePictureComponent(
        componentContext = childContext("profile picture"),
        setPath = {}
    )
}