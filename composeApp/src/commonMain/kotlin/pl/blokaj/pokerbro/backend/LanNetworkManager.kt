package pl.blokaj.pokerbro.backend

expect class LanNetworkManager {
    fun getIpAddresses(): List<String>
    fun getBroadcastAddresses(): List<String>
    suspend fun withBroadcastLock(block: suspend () -> Unit)
    suspend fun ensureGranted(): Boolean
}