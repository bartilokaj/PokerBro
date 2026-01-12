package pl.blokaj.pokerbro.backend

import java.net.Inet4Address
import java.net.InterfaceAddress
import java.net.NetworkInterface

actual class LanNetworkManager {
    private var validIpAddress: List<InterfaceAddress> = emptyList()

    private fun updateIpAddresses(): Unit {
        validIpAddress = NetworkInterface.getNetworkInterfaces()
            .toList()
            .filter {
                it.isUp &&
                !it.isVirtual &&
                !it.isLoopback
            }
            .flatMap { networkInterface ->
                networkInterface.interfaceAddresses.filter { address ->
                    address.address is Inet4Address && !address.address.isLinkLocalAddress
                }
            }
    }
    actual fun getIpAddresses(): List<String> {
        updateIpAddresses()
        return validIpAddress.map { it.address.hostAddress }
    }

    actual fun getBroadcastAddresses(): List<String> {
        if (validIpAddress.isEmpty()) updateIpAddresses()
        return validIpAddress.map { it.broadcast.hostAddress }
    }

    actual suspend fun withBroadcastLock(block: suspend () -> Unit) {
        block()
    }

    actual suspend fun ensureGranted(): Boolean = true
}