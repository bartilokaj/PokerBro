package pl.blokaj.pokerbro.ui.items.interfaces

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow

interface ListComponent<T> {
    val model: Value<List<T>>
    val toStringFn: (T) -> String
    var onElementClicked: (T) -> Unit
}