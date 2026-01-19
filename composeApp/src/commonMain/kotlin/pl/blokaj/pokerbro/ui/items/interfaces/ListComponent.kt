package pl.blokaj.pokerbro.ui.items.interfaces

import kotlinx.coroutines.flow.StateFlow

interface ListComponent<T> {
    val model: StateFlow<List<T>>
    val listTitle: String
    val toStringFn: (T) -> String
    var onElementClicked: (T) -> Unit
}