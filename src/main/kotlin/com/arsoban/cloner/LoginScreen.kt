package com.arsoban.cloner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.javacord.api.AccountType
import org.javacord.api.DiscordApiBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletionException
import kotlin.concurrent.thread

class LoginScreen : KoinComponent {

    private val interfaceColor by inject<InterfaceColor>(named("interfaceColor"))

    private val programData by inject<ProgramData>(named("programData"))

    private val scaffoldState by inject<ScaffoldState>(named("scaffoldState"))

    private val coroutineScope by inject<CoroutineScope>(named("coroutineScope"))

    @Composable
    fun loginScreen() {

        AnimatedVisibility(
            visible = !programData.isLogged.value,
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
                    .background(interfaceColor.backgroundColor)
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

                        val textSelectionColors = TextSelectionColors(
                            handleColor = interfaceColor.firstInterfaceColor,
                            backgroundColor = interfaceColor.firstInterfaceColor.copy(alpha = 0.4f)
                        )

                        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {

                            OutlinedTextField(
                                value = programData.tokenField.value,
                                onValueChange = { text ->
                                    programData.tokenField.value = text
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
                                    Text("Token")
                                }
                            )

                        }

                        Button(
                            onClick = {
                                thread {
                                    programData.isLoginButtonActive.value = false

                                    try {
                                        programData.api = DiscordApiBuilder()
                                            .setAccountType(AccountType.CLIENT)
                                            .setToken(programData.tokenField.value)
                                            .login().join();

                                        programData.isLogged.value = true
                                        programData.isDisconnectButtonActive.value = true
                                    } catch (exc: CompletionException) {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar("Error, invalid token specified.")
                                        }
                                        programData.isLoginButtonActive.value = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                interfaceColor.secondInterfaceColor
                            ),
                            enabled = programData.isLoginButtonActive.value
                        ) {
                            Text("Login")
                        }
                    }

                }
            }


        }

    }

}