package pl.blokaj.pokerbro.utility

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread-safe wrapper around [HashSet] using a [Mutex].
 */
class ThreadSafeSet<T>(
    private val set: MutableSet<T>
) {
    private val mutex = Mutex()

    /** Adds [element] to the set. Returns true if it was not already present. */
    suspend fun add(element: T): Boolean = mutex.withLock {
        set.add(element)
    }

    /** Removes [element] from the set. Returns true if it was present. */
    suspend fun remove(element: T): Boolean = mutex.withLock {
        set.remove(element)
    }

    /** Returns an immutable copy of the current set. */
    suspend fun getImmutable(): Set<T> = mutex.withLock {
        set.toSet()
    }

    /** Clears all elements from the set. */
    suspend fun clear(): Unit = mutex.withLock {
        set.clear()
    }
}
