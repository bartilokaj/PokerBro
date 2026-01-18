package pl.blokaj.pokerbro.ui.items.components

import androidx.compose.runtime.mutableStateListOf
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.ui.items.interfaces.ListComponent

class FlowListComponent<T>(
    private val componentContext: ComponentContext,
    private val flow: MutableSharedFlow<T>,
    scope: CoroutineScope,
    override var onElementClicked: (T) -> Unit,
    override val toStringFn: (T) -> String = { it.toString() }
): ListComponent<T>, ComponentContext by componentContext {
    private val _elements = mutableStateListOf<T>()
    override val model: Value<List<T>> = MutableValue(emptyList())

    private val log = Logger.withTag("ListComponent")

    init {
        scope.launch {
            flow.collect { element ->
                log.i { "Received new lobby: $element" }
                _elements.add(element)
                (model as MutableValue).value = _elements.toList()
            }
        }
    }

    fun setOnClickFunction(newFun: (T) -> Unit) {
        onElementClicked = newFun
    }
}