package icu.takeneko.omms.central.graphics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    val scrollState = rememberLazyListState()
    var autoScroll by remember { mutableStateOf(GuiConfig.config.autoScroll) }
    var showSettingPage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    if (onValueUpdate == null) {
        logLines.addAll(logCache)
        coroutineScope.launch {
            scrollState.animateScrollToItem(logLines.size)
        }
        logCache.clear()
        onValueUpdate = {
            logLines.add(it)
            if (autoScroll) {
                coroutineScope.launch {
                    while (scrollState.canScrollForward){
                        scrollState.animateScrollToItem(logLines.size)
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
                    SelectionContainer {
                        LazyColumn(
                            state = scrollState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(logLines) {
                                Text(
                                    text = buildAnnotatedString {
                                        val time = it.substringBefore(']') + ']'
                                        withStyle(SpanStyle(
                                            color = Color(114, 171, 108)
                                        )) {
                                            append(time)
                                        }
                                        val lineWithOutTime = it.substringAfter(']')
                                        if (
                                            lineWithOutTime.contains("WARN")
                                            || lineWithOutTime.contains("ERROR")
                                        ) {
                                            withStyle(SpanStyle(
                                                color = MaterialTheme.colorScheme.onError
                                            )) {
                                                append(lineWithOutTime)
                                            }
                                        } else {
                                            append(lineWithOutTime)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = if (
                                                it.contains("WARN")
                                                || it.contains("ERROR")
                                            ) {
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                )
                            }
                        }
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
                                                scrollState.animateScrollToItem(logLines.size)
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
                exitApplication()
                CentralServer.stop()
            },
            title = Constants.PRODUCT_NAME
        ) {
            setMinimumSize(800.dp, 600.dp)
            guiElements()
        }
    }
}
