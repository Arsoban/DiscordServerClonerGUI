import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.javacord.api.AccountType
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.Icon
import org.javacord.api.entity.channel.ChannelCategory
import org.javacord.api.entity.channel.ChannelType
import java.awt.geom.RoundRectangle2D
import kotlin.concurrent.thread
import kotlin.system.exitProcess

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App(window: WindowScope, application: ApplicationScope) {

    val firstInterfaceColor = Color(233, 0, 17)
    val secondInterfaceColor = Color(158, 225, 0)
    val backgroundColor = Color(43, 43, 43)

    var isLogged by remember { mutableStateOf(false) }

    var tokenFiled by remember { mutableStateOf("") }

    var serverIdField by remember { mutableStateOf("") }

    var logsList = remember { mutableStateListOf<String>() }

    var api: DiscordApi? = null

    var lazyListState = rememberLazyListState()

    val bahnschrift = FontFamily(
        Font(
            resource = "fonts/bahnschrift.ttf"
        )
    )

    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {

        Scaffold(
            topBar = {

                window.WindowDraggableArea {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Text(
                                            "DiscordServerClonerGUI",
                                            modifier = Modifier
                                                .align(Alignment.CenterStart),
                                            fontFamily = bahnschrift,
                                            color = Color(255, 255, 255)
                                        )

                                        IconButton(
                                            onClick = {
                                                exitProcess(0)
                                            },
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                        ) {
                                            Icon(
                                                painterResource("images/exit.png"),
                                                "Exit"
                                            )
                                        }
                                    }

                                }

                            }
                        },
                        backgroundColor = firstInterfaceColor
                    )
                }
            }
        ) {


            AnimatedVisibility(
                visible = !isLogged,
                enter = slideInHorizontally { offset ->
                    offset
                },
                exit = slideOutHorizontally { offset ->
                    offset
                },
                modifier = Modifier
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                ){
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    ) {

                        Column(

                        ) {

                            Text(
                                "DiscordServerClonerGUI",
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                color = Color(255, 255, 255)
                            )

                            Text(
                                "Enter your token below!",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                color = Color(255, 255, 255)
                            )

                            OutlinedTextField(
                                value = tokenFiled,
                                onValueChange = { text ->
                                    tokenFiled = text
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color(255, 255, 255),
                                    focusedBorderColor = firstInterfaceColor,
                                    cursorColor = firstInterfaceColor,
                                    unfocusedBorderColor = firstInterfaceColor,
                                    focusedLabelColor = firstInterfaceColor,
                                    unfocusedLabelColor = Color(255, 255, 255)
                                ),
                                label = {
                                    Text("Token")
                                }
                            )

                            Button(
                                onClick = {
                                    thread {
                                        api = DiscordApiBuilder()
                                            .setAccountType(AccountType.CLIENT)
                                            .setToken(tokenFiled)
                                            .login().join();

                                        isLogged = true
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    secondInterfaceColor
                                )
                            ) {
                                Text("Login")
                            }
                        }

                    }
                }


            }


            AnimatedVisibility(
                visible = isLogged,
                enter = slideInHorizontally { offset ->
                    -offset
                },
                exit = slideOutHorizontally { offset ->
                    -offset
                }
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    ) {
                        Column(

                        ) {

                            Text(
                                "DiscordServerClonerGUI",
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                color = Color(255, 255, 255)
                            )

                            Text(
                                "Enter id of server you want to clone!",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                color = Color(255, 255, 255)
                            )

                            OutlinedTextField(
                                value = serverIdField,
                                onValueChange = { text ->
                                    serverIdField = text
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color(255, 255, 255),
                                    focusedBorderColor = firstInterfaceColor,
                                    cursorColor = firstInterfaceColor,
                                    unfocusedBorderColor = firstInterfaceColor,
                                    focusedLabelColor = firstInterfaceColor,
                                    unfocusedLabelColor = Color(255, 255, 255)
                                ),
                                label = {
                                    Text("Server ID")
                                }
                            )

                            Button(
                                onClick = {
                                    thread {
                                        clone(serverIdField.toLong(), api!!, logsList, lazyListState, coroutineScope)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    secondInterfaceColor
                                )
                            ) {
                                Text("Clone")
                            }

                            Button(
                                onClick = {
                                    thread {
                                        api!!.disconnect()

                                        isLogged = false
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    secondInterfaceColor
                                )
                            ) {
                                Text("Disconnect")
                            }

                            AnimatedVisibility(logsList.isNotEmpty()) {

                                Box {
                                    LazyColumn(
                                        state = lazyListState,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .size(300.dp)
                                    ) {

                                        items(logsList.size) { index ->

                                            TextBox(logsList[index])

                                            Spacer(
                                                modifier = Modifier.height(6.dp)
                                            )

                                        }

                                    }
                                }

                            }
                        }
                    }
                }
            }

        }

    }
}

fun clone(id: Long, api: DiscordApi, logs: SnapshotStateList<String>, state: LazyListState, coroutineScope: CoroutineScope) {

    var server = api.getServerById(id);

    logs.addAndUpdateList("Creating server...", state, coroutineScope)

    var newServerIcon: Icon? = try {
        server.get().icon.get();
    } catch (exc: NoSuchElementException){
        null;
    }

    var newServerId: Long;

    try {
        newServerId = api.createServerBuilder().apply {
            setName(server.get().name)
            newServerIcon?.let {
                setIcon(it)
            }
        }.create().join();
    } catch (exc: Exception){
        logs.addAndUpdateList("Error was found when creating server! Maybe you have 100 servers limit!", state, coroutineScope)
        return
    }

    Thread.sleep(3000);

    var newServer = api.getServerById(newServerId).get();

    logs.addAndUpdateList("Created server \"${newServer.name}\"", state, coroutineScope)

    logs.addAndUpdateList("Creating roles... (only this process is running to avoid bugs)", state, coroutineScope)

    var rolesThread = thread {
        server.get().roles.reversed().forEach { role ->
            newServer.createRoleBuilder().apply {
                setName(role.name)
                try {
                    setColor(role.color.get())
                } catch (exc: NoSuchElementException){
                    ;
                }
                setPermissions(role.permissions)
                setMentionable(role.isMentionable)
                setDisplaySeparately(role.isDisplayedSeparately)
            }.create().join().also {
                logs.addAndUpdateList("Created role \"${it.name}\"", state, coroutineScope)
            }
        }
    }

    rolesThread.join()

    logs.addAndUpdateList("Deleting default channels...", state, coroutineScope)

    var deleteDefaultChannelsThread = thread {
        newServer.channels.forEach { channel ->
            channel.delete().join();
        }
    }

    logs.addAndUpdateList("Creating channels...", state, coroutineScope)

    var currentCategory: ChannelCategory? = null;

    var createChannelsThread = thread {
        server.get().channels.forEach { channel ->
            when (channel.type){
                ChannelType.CHANNEL_CATEGORY -> {
                    var channelCategory = newServer.createChannelCategoryBuilder().apply {
                        setName(channel.name)
                    }.create().join().also {
                        logs.addAndUpdateList("Created category \"${it.name}\"", state, coroutineScope)
                        currentCategory = it;
                    }

                    logs.addAndUpdateList("Editing permissions for category \"${channelCategory.name}\"", state, coroutineScope)

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        channelCategory.createUpdater().addPermissionOverwrite(newServer.getRolesByName(api.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }

                }
                ChannelType.SERVER_TEXT_CHANNEL -> {
                    var textChannel = newServer.createTextChannelBuilder().apply {
                        setName(channel.name)
                        currentCategory?.let {
                            setCategory(it)
                        }
                    }.create().join().also {
                        logs.addAndUpdateList("Created text channel \"${it.name}\"", state, coroutineScope)
                    }

                    logs.addAndUpdateList("Editing permissions for text channel \"${textChannel.name}\"", state, coroutineScope)

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        textChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(api.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }

                }
                ChannelType.SERVER_VOICE_CHANNEL -> {
                    var voiceChannel = newServer.createVoiceChannelBuilder().apply {
                        setName(channel.name)
                        currentCategory?.let {
                            setCategory(it)
                        }
                    }.create().join().also {
                        logs.addAndUpdateList("Created voice channel \"${it.name}\"", state, coroutineScope)
                    }

                    logs.addAndUpdateList("Editing permissions for voice channel \"${voiceChannel.name}\"", state, coroutineScope)

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        voiceChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(api.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }
                }
                ChannelType.SERVER_STAGE_VOICE_CHANNEL -> {
                    logs.addAndUpdateList("Could not created stage channel :(", state, coroutineScope)
                }
                else -> {}
            }
        }
    }

    logs.addAndUpdateList("Creating emojis...", state, coroutineScope)

    var createEmojisThread = thread {
        server.get().customEmojis.forEach { emoji ->
            newServer.createCustomEmojiBuilder().apply {
                setName(emoji.name)
                setImage(emoji.image)
            }.create().join().also {
                logs.addAndUpdateList("Created emoji \"${it.name}\"", state, coroutineScope)
            }
        }
    }

    deleteDefaultChannelsThread.join();
    createChannelsThread.join();
    createEmojisThread.join();


    logs.addAndUpdateList("Server cloned :)", state, coroutineScope)

}

@Composable
fun TextBox(text: String = "Item") {
    Box(
        modifier = Modifier
            .height(45.dp)
            .width(400.dp)
            .background(color = Color(230, 230, 230, 50))
            .padding(start = 10.dp)
            .clip(RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
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
        window.shape = RoundRectangle2D.Double(0.0, 0.0, 600.0, 600.0, 50.0, 50.0)

        App(this@Window, this@application)
    }
}

fun SnapshotStateList<String>.addAndUpdateList(elem: String, state: LazyListState, coroutineScope: CoroutineScope) {

    add(elem)

    coroutineScope.launch {
        state.scrollToItem(size - 1)
    }


}