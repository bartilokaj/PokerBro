package pl.blokaj.pokerbro.utility

fun interfaceBroadcastAddress(ip: String, prefix: Int): String {
    val split = ip.split('.')
    var ipInt = 0
    ipInt += (split[0].toInt() and 0xFF) shl 24
    ipInt += (split[1].toInt() and 0xFF) shl 16
    ipInt += (split[2].toInt() and 0xFF) shl 8
    ipInt += (split[3].toInt() and 0xFF)

    val mask = if (prefix == 0) 0 else (-1 shl (32 - prefix))
    val network = ipInt and mask
    val broadcast = network or mask.inv()

    val broadcastStringAddress = listOf(
        (broadcast ushr 24) and 0xFF,
        (broadcast ushr 16) and 0xFF,
        (broadcast ushr 8) and 0xFF,
        broadcast and 0xFF
    ).joinToString(".")
    return broadcastStringAddress
}