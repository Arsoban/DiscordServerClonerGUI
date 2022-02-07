package com.arsoban.cloner

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.javacord.api.DiscordApi

data class ProgramData(
    var api: DiscordApi? = null,
    var isLogged: MutableState<Boolean> = mutableStateOf(false),
    var tokenField: MutableState<String> = mutableStateOf(""),
    var serverIdField: MutableState<String> = mutableStateOf(""),
    var logsList: SnapshotStateList<String> = mutableStateListOf(),
    var isLoginButtonActive: MutableState<Boolean> = mutableStateOf(true),
    var isDisconnectButtonActive: MutableState<Boolean> = mutableStateOf(false),
    var isMessagesCloningEnabled: MutableState<Boolean> = mutableStateOf(false)
)
