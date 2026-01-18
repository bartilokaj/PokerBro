package pl.blokaj.pokerbro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.DefaultComponentContext
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.ui.screens.components.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val context = DefaultComponentContext(lifecycle)
        val lanNetworkManager = LanNetworkManager(this)
        val rootComponent = RootComponent(context, lanNetworkManager)
        setContent {
            App(rootComponent)
        }
    }
}


//@Preview
//@Composable
//fun AppAndroidPreview() {
//    val context = DefaultComponentContext()
//    HostingScreen()
//}