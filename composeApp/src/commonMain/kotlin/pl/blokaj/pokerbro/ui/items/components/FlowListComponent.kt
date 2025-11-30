package pl.blokaj.pokerbro.ui.items.components

import androidx.compose.runtime.mutableStateListOf
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
    componentContext: ComponentContext,
    flow: MutableSharedFlow<T>,
    private val elementClicked: (T) -> Unit
): ListComponent<T>, ComponentContext by componentContext {
    private val _elements = mutableStateListOf<T>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override val model: Value<List<T>> = MutableValue(emptyList())


    init {
        scope.launch {
            flow.collect { element ->
                _elements.add(element)
                println("added $element")
                (model as MutableValue).value = _elements.toList()
            }
        }
    }

    fun onDestroy() {
        scope.cancel()
    }

    override fun onElementClicked(element: T) {
        elementClicked(element)
    }
}