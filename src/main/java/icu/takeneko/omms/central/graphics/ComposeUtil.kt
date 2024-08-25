package icu.takeneko.omms.central.graphics

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawableResource.asIcon(): Painter {
    return painterResource(this)
}