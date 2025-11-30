package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import pl.blokaj.pokerbro.ui.services.implementations.PlaceholderPicker


class MainFlowComponent(
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    val joiningComponent = JoiningComponent(componentContext, PlaceholderPicker())
    val homeComponent = HomeComponent(componentContext)
    val hostingComponent = HostingComponent(componentContext)
    private val _currentFlowScreen = MutableValue<FlowScreen>(FlowScreen.Home)
    val currentFlowScreen: Value<FlowScreen> get() = _currentFlowScreen

    fun goToJoining() {
        println("Changed from ${_currentFlowScreen.value} to Joining")
        _currentFlowScreen.value = FlowScreen.Joining
    }

    fun goToHome() {
        println("Changed from ${_currentFlowScreen.value} to Home")
        _currentFlowScreen.value = FlowScreen.Home
    }

    fun goToHosting() {
        println("Changed from ${_currentFlowScreen.value} to Hosting")
        _currentFlowScreen.value = FlowScreen.Hosting
    }

}

sealed class FlowScreen {
    object Home: FlowScreen()
    object Joining: FlowScreen()
    object Hosting: FlowScreen()
}