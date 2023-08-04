package net.zhuruoling.omms.central.gui

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import net.zhuruoling.omms.central.util.Util
import org.jetbrains.skia.*
import org.jetbrains.skiko.*
import top.colter.skiko.*
import top.colter.skiko.data.LayoutAlignment
import top.colter.skiko.data.Shadow
import top.colter.skiko.layout.*
import java.awt.Dimension
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

val randomSeed = SimpleDateFormat("YYYYMMDD").format(Date()).toLong()
val rpValue = Random(randomSeed).nextInt(100)
val hkString: String = getHitokoto()

fun getHitokoto(): String {
    val client = HttpClient(engineFactory = CIO) {

    }
    val url = "https://v1.hitokoto.cn/?encode=text"
    return runBlocking {
        return@runBlocking String(client.get(URL(url)).readBytes())
    }
}

val background = File(Util.joinFilePaths("bg.jpeg")).readBytes().run { Image.makeFromEncoded(this) }

fun main() {
    val img = View(
        modifier = Modifier()
            .width(1280.dp)
            .height(720.dp)
            .background(image = background)
    ) {
        Image(image = background, modifier = Modifier().width(1280.dp).height(720.dp))
        Column(
            Modifier()
                .fillMaxWidth() // 继承父元素宽度
                .margin(horizontal = 20.dp, vertical = 30.dp)
                .padding(20.dp)
                .background(Color.WHITE.withAlpha(0.8f))
                .border(3.dp, 15.dp, Color.WHITE.withAlpha(0.8f))
                .shadows(Shadow.ELEVATION_2)
        ) {
            Row(
                Modifier()
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp)
                    .background(Color.WHITE.withAlpha(0.5f))
                    .border(3.dp, 15.dp, Color.WHITE.withAlpha(0.2f))
                    .shadows(Shadow.ELEVATION_3)
            ) {
                Box(Modifier().width(80.dp).fillMaxHeight()) {
                    Box(
                        Modifier()
                            .width(80.dp)
                            .height(80.dp)
                            .background(Color.CYAN.withAlpha(0.6f))
                            .border(3.dp, 40.dp)
                    )
                    Box(
                        modifier = Modifier()
                            .width(20.dp)
                            .height(20.dp)
                            .background(Color.YELLOW)
                            .border(1.dp, 10.dp),
                        alignment = LayoutAlignment.BOTTOM_RIGHT
                    )
                }
                Column(modifier = Modifier().fillMaxHeight().fillMaxWidth().padding(horizontal = 10.dp)) {
                    Box(modifier = Modifier().fillMaxWidth().fillMaxHeight()) {
                        Text("今日人品： $rpValue", alignment = LayoutAlignment.CENTER_LEFT)
                    }
                }
            }
            Row(
                Modifier()
                    .fillMaxWidth()
                    .height(100.dp)
                    .margin(vertical = 20.dp)
                    .padding(10.dp)
                    .background(Color.WHITE.withAlpha(0.5f))
                    .border(3.dp, 15.dp, Color.WHITE)
                    .shadows(Shadow.ELEVATION_3)
            ) {
                Column(modifier = Modifier().fillMaxHeight().fillMaxWidth().padding(horizontal = 10.dp)) {
                    Box(modifier = Modifier().fillMaxWidth().fillMaxHeight()) {
                        Text(
                            "一言： ${hkString}",
                            alignment = LayoutAlignment.CENTER_LEFT
                        )
                    }
                }
            }
        }
    }
    val paint = Paint().apply {
        color = Color.WHITE
        this.isAntiAlias = true
    }
    val skiaLayer = SkiaLayer()
    val view = object : SkikoView {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            //canvas.drawImageClip(background, RRect.makeLTRB(0f, 0f, 1280f, 720f, 2f, 2f, 2f, 2f), paint)
            canvas.drawImage(img, 0f, 0f, paint)
        }
    }
    File("out.png").writeBytes(img.encodeToData()!!.bytes)

    skiaLayer.skikoView = GenericSkikoView(skiaLayer, view)

    SwingUtilities.invokeLater {
        val window = JFrame("Skiko example").apply {
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            preferredSize = Dimension(1280, 720)
        }
        skiaLayer.attachTo(window.contentPane)
        skiaLayer.needRedraw()
        window.pack()
        window.isVisible = true
    }
}