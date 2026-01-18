package pl.blokaj.pokerbro.ui.items.interfaces

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ListComponent<T> {
    val model: StateFlow<List<T>>
    val toStringFn: (T) -> String
    var onElementClicked: (T) -> Unit
}