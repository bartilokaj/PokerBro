package pl.blokaj.pokerbro.utility

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ThreadSafeSet<T> {
    private val set = HashSet<T>()
    private val mutex = Mutex()


    suspend fun add(element: T): Boolean = mutex.withLock {
        return set.add(element)
    }

    suspend fun remove(element: T): Boolean = mutex.withLock {
        set.remove(element)
    }

    suspend fun getImmutable(): Set<T> = mutex.withLock{
        return set.toSet()
    }

    suspend fun clear(): Unit = mutex.withLock {
        set.clear()
    }
}