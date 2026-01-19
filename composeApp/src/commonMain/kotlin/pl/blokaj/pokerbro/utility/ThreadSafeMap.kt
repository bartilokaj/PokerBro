package pl.blokaj.pokerbro.utility

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread-safe wrapper around [HashMap] using a [Mutex].
 */
class ThreadSafeMap<K, V>(
    private val map: MutableMap<K, V>
) {
    private val mutex = Mutex()

    /** Sets the value for the given [key]. */
    suspend fun set(key: K, value: V): Unit = mutex.withLock {
        map[key] = value
    }

    /** Returns the value associated with [key], or null if not present. */
    suspend fun get(key: K): V? = mutex.withLock {
        map[key]
    }

    /** Removes the value associated with [key] and returns it, or null if not present. */
    suspend fun remove(key: K): V? = mutex.withLock {
        map.remove(key)
    }

    /** Returns an immutable copy of the current map. */
    suspend fun getImmutable(): Map<K, V> = mutex.withLock {
        map.toMap()
    }

    /** Clears all entries from the map. */
    suspend fun clear(): Unit = mutex.withLock {
        map.clear()
    }
}
