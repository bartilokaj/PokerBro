package pl.blokaj.pokerbro.backend.client

enum class ConnectionState {
    PRECONNECTION,
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    FAILED
}