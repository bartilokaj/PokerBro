package pl.blokaj.pokerbro.backend.host.ktor

import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConnectionManager(private val managerScope: CoroutineScope) {
    private val connections: MutableSet<DefaultWebSocketSession> = mutableSetOf()
    private val mutex = Mutex()

    suspend fun add(connection: DefaultWebSocketSession) = mutex.withLock {
        connections.add((connection))
    }

    suspend fun remove(connection: DefaultWebSocketSession) = mutex.withLock {
        connections.remove(connection)
    }

    suspend fun broadcast(frame: Frame) = coroutineScope {
        val currentSet = mutex.withLock {
            connections.toSet()
        }

        currentSet.forEach { connection ->
            managerScope.launch {
                try {
                    connection.send(frame.copy())
                } catch (e: Exception) {
                    println("failed to send to ${e.message}")
                    remove(connection)
                }
            }
        }
    }


}