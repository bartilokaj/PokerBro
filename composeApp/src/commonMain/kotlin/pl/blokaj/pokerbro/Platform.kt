package pl.blokaj.pokerbro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform