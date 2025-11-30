package pl.blokaj.pokerbro

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object serverLogBus {
    val logs = MutableSharedFlow<String>()
}

object clientLogBus {
    val logs = MutableSharedFlow<String>()
}