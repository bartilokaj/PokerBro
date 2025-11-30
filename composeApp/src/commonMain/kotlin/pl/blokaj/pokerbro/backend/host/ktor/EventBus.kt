package pl.blokaj.pokerbro.backend.host.ktor

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pl.blokaj.pokerbro.backend.shared.Event

class EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun produceEvent(event: Event) {
        _events.emit(event)
    }
}