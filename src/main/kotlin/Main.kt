// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.application
import org.javacord.api.AccountType
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.Icon
import org.javacord.api.entity.channel.ChannelCategory
import org.javacord.api.entity.channel.ChannelType
import kotlin.concurrent.thread
import kotlin.system.exitProcess

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

    val bahnschrift = FontFamily(
        Font(
            resource = "fonts/bahnschrift.ttf"
        )
    )


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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                ) {

                    Text(
                        "DiscordServerClonerGUI",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(6.dp),
                        color = Color(255, 255, 255)
                    )

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

                        Column(

                        ) {
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
                                    unfocusedBorderColor = firstInterfaceColor
                                )
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

                    AnimatedVisibility(
                        visible = isLogged,
                        enter = slideInHorizontally { offset ->
                            -offset
                        },
                        exit = slideOutHorizontally { offset ->
                            -offset
                        }
                    ) {
                        Column(

                        ) {

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
                                    unfocusedBorderColor = firstInterfaceColor
                                )
                            )

                            Button(
                                onClick = {
                                    clone(serverIdField.toLong(), api!!, logsList)
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
                        }
                    }

                }
            }

        }

    }
}

fun clone(id: Long, api: DiscordApi, logs: MutableList<String>) {

    var server = api.getServerById(id);

    logs.add("Creating server...")

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
        logs.add("Error was found when creating server! Maybe you have 100 servers limit!")
        return
    }

    Thread.sleep(3000);

    var newServer = api.getServerById(newServerId).get();

    logs.add("Created server \"${newServer.name}\"")

    logs.add("Creating roles... (only this process is running to avoid bugs)")

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
                logs.add("Created role \"${it.name}\"")
            }
        }
    }

    rolesThread.join()

    logs.add("Deleting default channels...")

    var deleteDefaultChannelsThread = thread {
        newServer.channels.forEach { channel ->
            channel.delete().join();
        }
    }

    logs.add("Creating channels...")

    var currentCategory: ChannelCategory? = null;

    var createChannelsThread = thread {
        server.get().channels.forEach { channel ->
            when (channel.type){
                ChannelType.CHANNEL_CATEGORY -> {
                    var channelCategory = newServer.createChannelCategoryBuilder().apply {
                        setName(channel.name)
                    }.create().join().also {
                        logs.add("Created category \"${it.name}\"")
                        currentCategory = it;
                    }

                    logs.add("Editing permissions for category \"${channelCategory.name}\"")

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
                        logs.add("Created text channel \"${it.name}\"")
                    }

                    logs.add("Editing permissions for text channel \"${textChannel.name}\"")

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
                        logs.add("Created voice channel \"${it.name}\"")
                    }

                    logs.add("Editing permissions for voice channel \"${voiceChannel.name}\"")

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        voiceChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(api.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }
                }
                ChannelType.SERVER_STAGE_VOICE_CHANNEL -> {
                    logs.add("Could not created stage channel :(")
                }
                else -> {}
            }
        }
    }

    logs.add("Creating emojis...")

    var createEmojisThread = thread {
        server.get().customEmojis.forEach { emoji ->
            newServer.createCustomEmojiBuilder().apply {
                setName(emoji.name)
                setImage(emoji.image)
            }.create().join().also {
                logs.add("Created emoji \"${it.name}\"")
            }
        }
    }

    deleteDefaultChannelsThread.join();
    createChannelsThread.join();
    createEmojisThread.join();


    logs.add("Server cloned :)")

}

@Composable
fun TextBox(text: String = "Item") {
    Box(
        modifier = Modifier
            .height(45.dp)
            .width(400.dp)
            .background(color = Color(230, 230, 230, 50))
            .padding(start = 10.dp),
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
        undecorated = true
    ) {
        App(this@Window, this@application)
    }
}
