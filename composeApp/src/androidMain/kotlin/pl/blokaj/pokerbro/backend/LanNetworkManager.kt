package pl.blokaj.pokerbro.backend

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.blokaj.pokerbro.utility.interfaceBroadcastAddress
import java.net.Inet4Address
import java.net.InetAddress
import kotlin.coroutines.resume

actual class LanNetworkManager(
    private val activity: ComponentActivity
) {
    private var continuation: CancellableContinuation<Boolean>? = null
    private var wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var multicastLock = wifiManager.createMulticastLock("udp-discovery").apply { setReferenceCounted(false) }
    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            continuation?.resume(granted)
            continuation = null
        }
    private var ipAddresses: List<String> = emptyList();
    private var broadcastAddresses: List<String> = emptyList();


    private fun updateAddresses(): Unit {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return
        val linkProperties = connectivityManager.getLinkProperties(network) ?: return

        val validInterfaces = linkProperties.linkAddresses
            .filter {
                it.address is Inet4Address &&
                !it.address.isLinkLocalAddress &&
                !it.address.isLoopbackAddress}

        ipAddresses = validInterfaces.mapNotNull { it.address.hostAddress }
        broadcastAddresses = validInterfaces.mapNotNull {
            interfaceBroadcastAddress(it.address.hostAddress, it.prefixLength)
        }
    }

    actual fun getIpAddresses(): List<String> {
        updateAddresses()
        return ipAddresses
    }

    actual fun getBroadcastAddresses(): List<String> {
        if (broadcastAddresses.isEmpty()) updateAddresses()
        return broadcastAddresses
    }

    actual suspend fun withBroadcastLock(block: suspend () -> Unit) {
        multicastLock.acquire()
        try {
            block()
        } finally {
          multicastLock.release()
        }
    }

    actual suspend fun ensureGranted(): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true

        val permission = Manifest.permission.NEARBY_WIFI_DEVICES

        if (
            ContextCompat.checkSelfPermission(activity, permission)
            == PackageManager.PERMISSION_GRANTED
        ) return true

        return suspendCancellableCoroutine { cont ->
            continuation = cont
            launcher.launch(permission)
        }
    }
}