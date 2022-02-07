import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.arsoban.cloner.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module


@Composable
@Preview
fun App() {

    val interfaceColor = InterfaceColor()

    val fonts = Fonts()

    val coroutineScope = rememberCoroutineScope()

    val programData = ProgramData()

    val lazyListState = rememberLazyListState()

    val scaffoldState = rememberScaffoldState()

    val utilModule = module {

        single(named("interfaceColor")) { interfaceColor }
        single(named("fonts")) { fonts }
        single(named("coroutineScope")) { coroutineScope }
        single(named("programData")) { programData }
        single(named("lazyListState")) { lazyListState }
        single(named("scaffoldState")) { scaffoldState }

    }

    loadKoinModules(utilModule)

    val scaffoldSettings = ScaffoldSettings()

    MaterialTheme {

        Scaffold(
            topBar = scaffoldSettings.appTopBar,
            scaffoldState = scaffoldState
        ) {

            LoginScreen().loginScreen()

            CloneScreen().cloneScreen()

        }

    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DiscordServerClonerGUI",
        icon = painterResource("images/clone.png"),
        undecorated = true,
        resizable = false,
        state = WindowState(size = DpSize(600.dp, 600.dp))
    ) {

        val mainModule = module {

            single(named("window")) { this@Window }
            single(named("application")) { this@application }

        }

        startKoin {

            modules(mainModule)

        }

        App()
    }
}
