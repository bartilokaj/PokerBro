package pl.blokaj.pokerbro.utility

import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ThreadSafeMap<K, V> {
    private val map = HashMap<K, V>()
    private val mutex = Mutex()


    suspend fun set(key: K, value: V): Unit = mutex.withLock {
        map[key] = value
    }

    suspend fun get(key: K): V? = mutex.withLock {
        map[key]
    }

    suspend fun remove(key: K): V? = mutex.withLock {
        map.remove(key)
    }

    suspend fun getImmutable(): Map<K, V> = mutex.withLock{
        map.toMap()
    }

    suspend fun clear(): Unit = mutex.withLock {
        map.clear()
    }
}