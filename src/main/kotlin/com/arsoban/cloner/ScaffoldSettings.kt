package com.arsoban.cloner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.FrameWindowScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.system.exitProcess

class ScaffoldSettings : KoinComponent {

    private val window by inject<FrameWindowScope>(named("window"))

    private val interfaceColor by inject<InterfaceColor>(named("interfaceColor"))

    private val fonts by inject<Fonts>(named("fonts"))

    val appTopBar: @Composable () -> Unit = {

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
                                    fontFamily = fonts.bahnschrift,
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
                backgroundColor = interfaceColor.firstInterfaceColor
            )
        }
    }

}