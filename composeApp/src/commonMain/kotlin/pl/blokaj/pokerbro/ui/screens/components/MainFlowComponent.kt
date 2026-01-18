package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import pl.blokaj.pokerbro.ui.services.implementations.PlaceholderPicker
import kotlin.String


class MainFlowComponent(
    private val componentContext: ComponentContext,
    private val initialPlayerName: Value<String>,
    private val initialPlayerPicturePath: Value<String>,
    private val initialLobbyName: Value<String>,
    private val initialStartingFunds: Value<Int>,
    private val onLobbySearch: (playerName: String) -> Unit,
    private val onHostingStart:  (playerName: String, lobbyName: String, startingFunds: Int) -> Unit,
    private val onWrongInput: (reason: String) -> Unit
): ComponentContext by componentContext {
    private val flowScreenStack = StackNavigation<FlowScreen>()


    val flowChildStack = componentContext.childStack(
        source = flowScreenStack,
        childFactory = { config: FlowScreen, context: ComponentContext ->
            when (config) {
                FlowScreen.Home -> FlowChild.Home(HomeComponent(context))
                FlowScreen.Joining -> FlowChild.Joining(
                    JoiningComponent(
                        componentContext = context,
                        profilePicturePicker = PlaceholderPicker(),
                        initialPlayerName = initialPlayerName,
                        onLobbySearch = onLobbySearch,
                        onWrongInput = onWrongInput
                    )
                )
                FlowScreen.Hosting -> FlowChild.Hosting(LobbySetupComponent(
                        componentContext = context,
                        profilePicturePicker = PlaceholderPicker(),
                        initialPlayerName = initialPlayerName,
                        initialLobbyName = initialLobbyName,
                        initialStartingFunds = initialStartingFunds,
                        onHostingStart = onHostingStart,
                        onWrongInput = onWrongInput
                    )
                )
            }
        },
        serializer = null,
        initialStack = { listOf(FlowScreen.Home) }
    )


    fun goToJoining() {
        flowScreenStack.bringToFront(FlowScreen.Joining)
    }

    fun goToHome() {
        flowScreenStack.bringToFront(FlowScreen.Home)
    }

    fun goToHosting() {
        flowScreenStack.bringToFront(FlowScreen.Hosting)
    }

    sealed interface FlowScreen {
        object Home: FlowScreen
        object Joining: FlowScreen
        object Hosting: FlowScreen
    }

    sealed interface FlowChild {
        class Home(val component: HomeComponent) : FlowChild
        class Joining(val component: JoiningComponent) : FlowChild
        class Hosting(val component: LobbySetupComponent) : FlowChild
    }
}