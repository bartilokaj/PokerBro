package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.pushToFront
import pl.blokaj.pokerbro.ui.services.implementations.PlaceholderPicker


class MainFlowComponent(
    private val componentContext: ComponentContext,
    private val onLobbySearch: (String, String) -> Unit,
    private val onHostingStart:  (String, String) -> Unit,
    private val initialPlayerName: String,
    private val initialPlayerPicturePath: String
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
                        initialName = initialPlayerName,
                        onLobbySearch = onLobbySearch
                    )
                )
                FlowScreen.Hosting -> FlowChild.Hosting(HostingComponent(
                        componentContext = context,
                        profilePicturePicker = PlaceholderPicker(),
                        initialName = initialPlayerName,
                        onHostingStart = onHostingStart
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
        class Hosting(val component: HostingComponent) : FlowChild
    }
}