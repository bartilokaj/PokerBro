package pl.blokaj.pokerbro.backend.client.ktor

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ThreadSafeSet<T> {
    private val set = HashSet<T>()
    private val mutex = Mutex()


    suspend fun add(element: T): Boolean = mutex.withLock {
        return set.add(element)
    }

    suspend fun remove(element: T) = mutex.withLock {
        set.remove(element)
    }

    suspend fun getImmutable(): Set<T> = mutex.withLock{
        return set.toSet()
    }
}