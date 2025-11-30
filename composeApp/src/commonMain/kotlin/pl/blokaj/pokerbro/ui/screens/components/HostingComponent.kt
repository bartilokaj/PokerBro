package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import pl.blokaj.pokerbro.backend.host.ktor.startServer
import pl.blokaj.pokerbro.serverLogBus
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent

class HostingComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

}