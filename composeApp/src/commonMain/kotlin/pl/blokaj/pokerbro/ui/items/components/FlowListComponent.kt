package pl.blokaj.pokerbro.ui.items.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import pl.blokaj.pokerbro.ui.items.interfaces.ListComponent

class FlowListComponent<T>(
    private val componentContext: ComponentContext,
    flow: StateFlow<List<T>>,
    override val listTitle: String,
    override var onElementClicked: (T) -> Unit,
    override val toStringFn: (T) -> String = { it.toString() }
): ListComponent<T>, ComponentContext by componentContext {
    override val model = flow
}