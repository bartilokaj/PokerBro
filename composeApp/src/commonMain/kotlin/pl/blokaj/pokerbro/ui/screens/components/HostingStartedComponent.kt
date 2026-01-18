package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.PlayerData

class HostingStartedComponent(
    componentContext: ComponentContext,
): ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

}