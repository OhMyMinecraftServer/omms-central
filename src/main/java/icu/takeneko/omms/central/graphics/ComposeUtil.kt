package icu.takeneko.omms.central.graphics

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import ch.qos.logback.classic.Level
import icu.takeneko.omms.central.util.logging.LogEvent
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.text.SimpleDateFormat

@Composable
fun DrawableResource.asIcon(): Painter {
    return painterResource(this)
}

@Composable
fun convertLevelColor(level: Level): Color = when (level.levelInt) {
    Level.ERROR_INT -> Color(255, 91, 99)
    Level.WARN_INT -> Color(255, 201, 78)
    Level.INFO_INT -> Color(168, 205, 88)
    else -> Color(119, 205, 159)
}

@Composable
fun LogEvent.buildAnnotatedString() = androidx.compose.ui.text.buildAnnotatedString {
    withStyle(
        SpanStyle(
            color = Color(255, 201, 78)
        )
    ) {
        append(SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]").format(time))
    }

    withStyle(
        SpanStyle(
            color = convertLevelColor(logLevel)
        )
    ) {
        append(" [$thread/${logLevel.levelStr}]")
    }

    withStyle(
        SpanStyle(
            color = Color(119, 205, 159)
        )
    ) {
        append(" ($loggerName): ")
    }

    append(logMessage)
}