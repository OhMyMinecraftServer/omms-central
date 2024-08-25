package icu.takeneko.omms.central.graphics

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import icu.takeneko.omms.central.command.CommandManager
import icu.takeneko.omms.central.command.CommandSourceStack
import icu.takeneko.omms.central.fundation.Constants
import icu.takeneko.omms.central.main.CentralServer
import icu.takeneko.omms.central.util.logging.MemoryAppender
import icu.takeneko.omms_central.generated.resources.Res
import icu.takeneko.omms_central.generated.resources.dark_mode_24px
import icu.takeneko.omms_central.generated.resources.light_mode_24px
import icu.takeneko.omms_central.generated.resources.settings_24px
import kotlinx.coroutines.launch
import org.jetbrains.skiko.hostOs
import org.slf4j.LoggerFactory
import java.awt.Dimension

private val logger = LoggerFactory.getLogger("GuiMain")
private var onValueUpdate: ((String) -> Unit)? = null
private var logCache = mutableListOf<String>()

@Composable
fun FrameWindowScope.setMinimumSize(
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
) {
    val density = LocalDensity.current
    LaunchedEffect(density) {
        window.minimumSize = with(density) {
            if (hostOs.isWindows) {
                Dimension(width.toPx().toInt(), height.toPx().toInt())
            } else {
                Dimension(width.value.toInt(), height.value.toInt())
            }
        }
    }
}

@Composable
fun guiElements() {
    val logLines = remember { mutableStateListOf<String>() }
    val darkDefault = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(darkDefault) }
    var theme: ColorScheme by remember {
        mutableStateOf(
            if (isDarkTheme) {
                highContrastDarkColorScheme
            } else {
                lightScheme
            }
        )
    }
    var commandString by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var autoScroll by remember { mutableStateOf(GuiConfig.config.autoScroll) }
    var showSettingPage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    if (onValueUpdate == null) {
        logLines.addAll(logCache)
        coroutineScope.launch {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
        logCache.clear()
        onValueUpdate = {
            logLines.add(it)
            if (autoScroll) {
                coroutineScope.launch {
                    while (scrollState.canScrollForward){
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            }
        }
    }
    MaterialTheme(colorScheme = theme) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Box(
                        Modifier.padding(8.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = Constants.PRODUCT_NAME,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterEnd),
                        ) {
                            IconButton(onClick = {
                                showSettingPage = !showSettingPage
                            }) {
                                Icon(
                                    Res.drawable.settings_24px.asIcon(),
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = {
                                isDarkTheme = !isDarkTheme
                                theme = if (isDarkTheme) {
                                    highContrastDarkColorScheme
                                } else {
                                    lightScheme
                                }
                            }) {
                                Icon(
                                    (if (isDarkTheme) Res.drawable.dark_mode_24px else Res.drawable.light_mode_24px).asIcon(),
                                    "",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = logLines.joinToString("\n"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().onKeyEvent {
                        if (it.key == Key.Enter || it.key == Key.NumPadEnter) {
                            if (commandString.isNotBlank()) {
                                val command = commandString
                                CentralServer.runOnServerThread {
                                    CommandManager.INSTANCE.reload()
                                    CommandManager.INSTANCE.dispatchCommand(
                                        command,
                                        CommandSourceStack(CommandSourceStack.Source.CONSOLE)
                                    )
                                }
                                commandString = ""
                            }
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    },
                    value = commandString,
                    onValueChange = {
                        commandString = it
                    },
                    label = {
                        Text("Command")
                    },
                    singleLine = true
                )
            }
            AnimatedVisibility(
                visible = showSettingPage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showSettingPage = false
                        },
                    color = Color(0x55000000)
                ) {

                }
            }
            AnimatedVisibility(
                visible = showSettingPage,
                enter = slideIn {
                    IntOffset(-it.width, 0)
                },
                exit = slideOut {
                    IntOffset(-it.width, 0)
                }
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxHeight()
                        .wrapContentWidth()
                        .padding(vertical = 24.dp)
                        .padding(start = 24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp)
                            .padding(start = 12.dp, end = 98.dp)
                    ) {
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                showSettingPage = false
                            }, enabled = false) {
                                Icon(
                                    Res.drawable.settings_24px.asIcon(),
                                    "",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                modifier = Modifier.padding(vertical = 10.dp),
                                text = "Settings",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column(
                            modifier = Modifier.wrapContentSize().padding(start = 12.dp)
                        ){
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    checked = autoScroll,
                                    onCheckedChange = {
                                        autoScroll = it
                                        GuiConfig.config.autoScroll = it
                                        GuiConfig.save()
                                        if (it){
                                            coroutineScope.launch {
                                                scrollState.animateScrollTo(scrollState.maxValue)
                                            }
                                        }
                                    },
                                )
                                Text(
                                    modifier = Modifier.padding(start = 10.dp),
                                    text = "Auto Scroll"
                                )
                            }
                        }

                    }
                }

            }
        }
    }

}

fun guiMain() {
    GuiConfig.load()
    MemoryAppender.subscribe {
        if (onValueUpdate == null) {
            logCache.add(it)
        } else {
            onValueUpdate!!(it)
        }
    }
    application {
        Window(
            onCloseRequest = {
                CentralServer.stop()
            },
            title = Constants.PRODUCT_NAME
        ) {
            setMinimumSize(800.dp, 600.dp)
            guiElements()
        }
    }
}
