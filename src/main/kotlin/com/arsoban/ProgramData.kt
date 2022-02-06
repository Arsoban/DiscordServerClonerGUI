package com.arsoban

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
    var logsList: SnapshotStateList<String> = mutableStateListOf()
)
