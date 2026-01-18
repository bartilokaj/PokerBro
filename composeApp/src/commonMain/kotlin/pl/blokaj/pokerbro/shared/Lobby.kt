package pl.blokaj.pokerbro.shared

data class Lobby(
    val lobbyName: String,
    val hostName: String,
    val ip: String,
    val port: Int
)
