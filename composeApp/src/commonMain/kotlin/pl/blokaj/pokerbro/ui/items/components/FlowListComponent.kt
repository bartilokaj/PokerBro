package pl.blokaj.pokerbro.ui.items.components

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.ui.items.interfaces.ListComponent

class FlowListComponent<T>(
    private val componentContext: ComponentContext,
    private val flow: StateFlow<List<T>>,
    override var onElementClicked: (T) -> Unit,
    override val toStringFn: (T) -> String = { it.toString() }
): ListComponent<T>, ComponentContext by componentContext {
    override val model: StateFlow<List<T>> get() = flow
}