package com.arsoban.cloner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.minnced.discord.webhook.external.JavacordWebhookClient
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.javacord.api.entity.Icon
import org.javacord.api.entity.channel.ChannelCategory
import org.javacord.api.entity.channel.ChannelType
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.server.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*
import kotlin.NoSuchElementException
import kotlin.concurrent.thread

class CloneScreen : KoinComponent {

    private val interfaceColor by inject<InterfaceColor>(named("interfaceColor"))

    private val programData by inject<ProgramData>(named("programData"))

    private val coroutineScope by inject<CoroutineScope>(named("coroutineScope"))

    private val lazyListState by inject<LazyListState>(named("lazyListState"))

    @Composable
    fun cloneScreen() {

        AnimatedVisibility(
            visible = programData.isLogged.value,
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
                    .background(interfaceColor.backgroundColor)
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
                            "Logged as ${programData.api!!.yourself.discriminatedName}",
                            fontSize = 14.sp,
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

                        val textSelectionColors = TextSelectionColors(
                            handleColor = interfaceColor.firstInterfaceColor,
                            backgroundColor = interfaceColor.firstInterfaceColor.copy(alpha = 0.4f)
                        )

                        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {

                            OutlinedTextField(
                                value = programData.serverIdField.value,
                                onValueChange = { text ->
                                    programData.serverIdField.value = text
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(6.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color(255, 255, 255),
                                    focusedBorderColor = interfaceColor.firstInterfaceColor,
                                    cursorColor = interfaceColor.firstInterfaceColor,
                                    unfocusedBorderColor = interfaceColor.firstInterfaceColor,
                                    focusedLabelColor = interfaceColor.firstInterfaceColor,
                                    unfocusedLabelColor = Color(255, 255, 255)
                                ),
                                label = {
                                    Text("Server ID")
                                }
                            )

                        }


                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
//                                .padding(8.dp)
                        ) {

                            Checkbox(
                                checked = programData.isMessagesCloningEnabled.value,
                                onCheckedChange = { value ->
                                    programData.isMessagesCloningEnabled.value = value
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = interfaceColor.secondInterfaceColor,
                                    uncheckedColor = Color(255, 255, 255)
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )

                            Text(
                                "Message Cloning",
                                color = Color(255, 255, 255),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )

                        }

                        Button(
                            onClick = {
//                                    clone(serverIdField.toLong(), api!!, logsList, lazyListState, coroutineScope)
                                GlobalScope.launch {

                                    if (programData.isMessagesCloningEnabled.value) {

                                        cloneWithMessages(programData.serverIdField.value.toLong())

                                    } else {

                                        clone(programData.serverIdField.value.toLong())

                                    }

                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                interfaceColor.secondInterfaceColor
                            )
                        ) {
                            Text("Clone")
                        }

                        Button(
                            onClick = {
                                thread {
                                    programData.isDisconnectButtonActive.value = false

                                    programData.api!!.disconnect()

                                    programData.isLogged.value = false
                                    programData.isLoginButtonActive.value = true
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                interfaceColor.secondInterfaceColor
                            ),
                            enabled = programData.isDisconnectButtonActive.value
                        ) {
                            Text("Disconnect")
                        }

                        AnimatedVisibility(programData.logsList.isNotEmpty()) {

                            Box {
                                LazyColumn(
                                    state = lazyListState,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .size(300.dp)
                                ) {

                                    items(programData.logsList.size) { index ->

                                        Box(
                                            modifier = Modifier
                                                .height(45.dp)
                                                .width(400.dp)
                                                .background(color = Color(230, 230, 230, 50))
                                                .padding(start = 10.dp)
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(text = programData.logsList[index])
                                        }

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

    private suspend fun clone(id: Long) {

        programData.logsList.addAndUpdateList("Starting server cloning...", lazyListState, coroutineScope)

        val server = programData.api!!.getServerById(id);

        programData.logsList.addAndUpdateList("Creating server...", lazyListState, coroutineScope)

        val newServerIcon: Icon? = try {
            server.get().icon.get();
        } catch (exc: NoSuchElementException){
            null;
        }

        var newServerId: Long? = null;

        try {
            newServerId = programData.api!!.createServerBuilder().apply {
                setName(server.get().name)
                newServerIcon?.let {
                    setIcon(it)
                }
            }.create().join();
        } catch (exc: Exception){
            programData.logsList.addAndUpdateList("Error was found when creating server! Maybe you have 100 servers limit!", lazyListState, coroutineScope)
            return;
        }

        delay(3000)

        val newServer = programData.api!!.getServerById(newServerId!!).get();

        programData.logsList.addAndUpdateList("Created server \"${newServer.name}\"", lazyListState, coroutineScope)

        programData.logsList.addAndUpdateList("Creating roles... (only this process is running to avoid bugs)", lazyListState, coroutineScope)

        val rolesCoroutine = GlobalScope.launch {
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
                    programData.logsList.addAndUpdateList("Created role \"${it.name}\"", lazyListState, coroutineScope)
                }
            }
        }

        rolesCoroutine.join()

        programData.logsList.addAndUpdateList("Deleting default channels...", lazyListState, coroutineScope)

        val deleteDefaultChannelsCoroutine = GlobalScope.launch {
            newServer.channels.forEach { channel ->
                channel.delete().join();
            }
        }

        programData.logsList.addAndUpdateList("Creating channels...", lazyListState, coroutineScope)

        var currentCategory: ChannelCategory? = null;

        val createChannelsCoroutine = GlobalScope.launch {
            server.get().channels.forEach { channel ->
                when (channel.type){
                    ChannelType.CHANNEL_CATEGORY -> {
                        val channelCategory = newServer.createChannelCategoryBuilder().apply {
                            setName(channel.name)
                        }.create().join().also {
                            programData.logsList.addAndUpdateList("Created category \"${it.name}\"", lazyListState, coroutineScope)
                            currentCategory = it;
                        }

                        programData.logsList.addAndUpdateList("Editing permissions for category \"${channelCategory.name}\"", lazyListState, coroutineScope)

                        channel.overwrittenRolePermissions.forEach { rolePermissions ->
                            channelCategory.createUpdater().addPermissionOverwrite(newServer.getRolesByName(programData.api!!.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                        }

                    }
                    ChannelType.SERVER_TEXT_CHANNEL -> {
                        val textChannel = newServer.createTextChannelBuilder().apply {
                            setName(channel.name)
                            currentCategory?.let {
                                setCategory(it)
                            }
                        }.create().join().also {
                            programData.logsList.addAndUpdateList("Created text channel \"${it.name}\"", lazyListState, coroutineScope)
                        }

                        programData.logsList.addAndUpdateList("Editing permissions for text channel \"${textChannel.name}\"", lazyListState, coroutineScope)

                        channel.overwrittenRolePermissions.forEach { rolePermissions ->
                            textChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(programData.api!!.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                        }

                    }
                    ChannelType.SERVER_VOICE_CHANNEL -> {
                        val voiceChannel = newServer.createVoiceChannelBuilder().apply {
                            setName(channel.name)
                            currentCategory?.let {
                                setCategory(it)
                            }
                        }.create().join().also {
                            programData.logsList.addAndUpdateList("Created voice channel \"${it.name}\"", lazyListState, coroutineScope)
                        }

                        programData.logsList.addAndUpdateList("Editing permissions for voice channel \"${voiceChannel.name}\"", lazyListState, coroutineScope)

                        channel.overwrittenRolePermissions.forEach { rolePermissions ->
                            voiceChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(programData.api!!.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                        }
                    }
                    ChannelType.SERVER_STAGE_VOICE_CHANNEL -> {
                        programData.logsList.addAndUpdateList("Could not created stage channel :(", lazyListState, coroutineScope)
                    }
                    else -> {}
                }
            }
        }

        programData.logsList.addAndUpdateList("Creating emojis...", lazyListState, coroutineScope)

        val createEmojisCoroutine = GlobalScope.launch {
            server.get().customEmojis.forEach { emoji ->
                newServer.createCustomEmojiBuilder().apply {
                    setName(emoji.name)
                    setImage(emoji.image)
                }.create().join().also {
                    programData.logsList.addAndUpdateList("Created emoji \"${it.name}\"", lazyListState, coroutineScope)
                }
            }
        }

        deleteDefaultChannelsCoroutine.join();
        createChannelsCoroutine.join();
        createEmojisCoroutine.join();


        programData.logsList.addAndUpdateList("Server cloned :)", lazyListState, coroutineScope)
    }


    private suspend fun cloneWithMessages(id: Long) {

        val oldNewTextChannels = mutableMapOf<ServerTextChannel, ServerTextChannel>()

        programData.logsList.addAndUpdateList("Starting server cloning with messages...", lazyListState, coroutineScope)

        val server = programData.api!!.getServerById(id);

        programData.logsList.addAndUpdateList("Creating server...", lazyListState, coroutineScope)

        val newServerIcon: Icon? = try {
            server.get().icon.get();
        } catch (exc: NoSuchElementException){
            null;
        }

        var newServerId: Long? = null;

        try {
            newServerId = programData.api!!.createServerBuilder().apply {
                setName(server.get().name)
                newServerIcon?.let {
                    setIcon(it)
                }
            }.create().join();
        } catch (exc: Exception){
            programData.logsList.addAndUpdateList("Error was found when creating server! Maybe you have 100 servers limit!", lazyListState, coroutineScope)
            return;
        }

        delay(3000)

        val newServer = programData.api!!.getServerById(newServerId!!).get();

        programData.logsList.addAndUpdateList("Created server \"${newServer.name}\"", lazyListState, coroutineScope)

        programData.logsList.addAndUpdateList("Creating roles... (only this process is running to avoid bugs)", lazyListState, coroutineScope)

        val rolesCoroutine = GlobalScope.launch {
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
                    programData.logsList.addAndUpdateList("Created role \"${it.name}\"", lazyListState, coroutineScope)
                }
            }
        }

        rolesCoroutine.join()

        programData.logsList.addAndUpdateList("Deleting default channels...", lazyListState, coroutineScope)

        val deleteDefaultChannelsCoroutine = GlobalScope.launch {
            newServer.channels.forEach { channel ->
                channel.delete().join();
            }
        }

        programData.logsList.addAndUpdateList("Creating channels...", lazyListState, coroutineScope)

        val createChannelsCoroutine = GlobalScope.launch {
            channelsFlow(server, newServer).collect() { textChannels ->

                val incomingWebhook = textChannels[1].createWebhookBuilder().apply {

                    setName("Cloner Helper")


                }.create().join()

                val webhookClient = JavacordWebhookClient.withUrl(incomingWebhook.url.toString())

                val messages = textChannels[0].messagesAsStream.toList().reversed()

                for (msg in messages) {

                    if (msg.content.isEmpty())
                        continue

                    try {
                        webhookClient.send(
                            WebhookMessageBuilder().apply {

                                setUsername(msg.userAuthor.get().name)
                                setAvatarUrl(msg.userAuthor.get().avatar.url.toString())

                                setContent(msg.content)

                            }.build()
                        )
                    } catch (_: Exception) {
                        ;
                    }

                }

            }
        }

        programData.logsList.addAndUpdateList("Creating emojis...", lazyListState, coroutineScope)

        val createEmojisCoroutine = GlobalScope.launch {
            server.get().customEmojis.forEach { emoji ->
                newServer.createCustomEmojiBuilder().apply {
                    setName(emoji.name)
                    setImage(emoji.image)
                }.create().join().also {
                    programData.logsList.addAndUpdateList("Created emoji \"${it.name}\"", lazyListState, coroutineScope)
                }
            }
        }

        deleteDefaultChannelsCoroutine.join();
        createChannelsCoroutine.join();
        createEmojisCoroutine.join();

        programData.logsList.addAndUpdateList("Server cloned :)", lazyListState, coroutineScope)
    }

    private fun channelsFlow(server: Optional<Server>, newServer: Server): Flow<List<ServerTextChannel>> = flow {

        var currentCategory: ChannelCategory? = null;

        server.get().channels.forEach { channel ->
            when (channel.type) {
                ChannelType.CHANNEL_CATEGORY -> {
                    val channelCategory = newServer.createChannelCategoryBuilder().apply {
                        setName(channel.name)
                    }.create().join().also {
                        programData.logsList.addAndUpdateList("Created category \"${it.name}\"", lazyListState, coroutineScope)
                        currentCategory = it;
                    }

                    programData.logsList.addAndUpdateList("Editing permissions for category \"${channelCategory.name}\"", lazyListState, coroutineScope)

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        channelCategory.createUpdater().addPermissionOverwrite(newServer.getRolesByName(programData.api!!.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }

                }
                ChannelType.SERVER_TEXT_CHANNEL -> {
                    val textChannel = newServer.createTextChannelBuilder().apply {
                        setName(channel.name)
                        currentCategory?.let {
                            setCategory(it)
                        }
                    }.create().join().also {
                        emit(listOf(channel as ServerTextChannel, it as ServerTextChannel))
                        programData.logsList.addAndUpdateList("Created text channel \"${it.name}\"", lazyListState, coroutineScope)
                    }

                    programData.logsList.addAndUpdateList("Editing permissions for text channel \"${textChannel.name}\"", lazyListState, coroutineScope)

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        textChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(programData.api!!.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }

                }
                ChannelType.SERVER_VOICE_CHANNEL -> {
                    val voiceChannel = newServer.createVoiceChannelBuilder().apply {
                        setName(channel.name)
                        currentCategory?.let {
                            setCategory(it)
                        }
                    }.create().join().also {
                        programData.logsList.addAndUpdateList("Created voice channel \"${it.name}\"", lazyListState, coroutineScope)
                    }

                    programData.logsList.addAndUpdateList("Editing permissions for voice channel \"${voiceChannel.name}\"", lazyListState, coroutineScope)

                    channel.overwrittenRolePermissions.forEach { rolePermissions ->
                        voiceChannel.createUpdater().addPermissionOverwrite(newServer.getRolesByName(programData.api!!.getRoleById(rolePermissions.key).get().name)[0], rolePermissions.value).update().join()
                    }
                }
                ChannelType.SERVER_STAGE_VOICE_CHANNEL -> {
                    programData.logsList.addAndUpdateList("Could not created stage channel :(", lazyListState, coroutineScope)
                }
                else -> {}
            }
        }

    }

}