package pl.blokaj.pokerbro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.DefaultComponentContext
import pl.blokaj.pokerbro.ui.screens.components.HostingComponent
import pl.blokaj.pokerbro.ui.screens.contents.HostingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        println("created activity")
        val context = DefaultComponentContext(lifecycle)
        setContent {
            App(context)
        }
    }
}

class HostingActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootContext = DefaultComponentContext(lifecycle)
        val hostingComponent = HostingComponent(rootContext)


        setContent {
            HostingScreen(hostingComponent)
        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    val context = DefaultComponentContext()
//    HostingScreen()
//}