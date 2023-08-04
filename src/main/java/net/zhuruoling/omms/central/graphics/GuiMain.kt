package net.zhuruoling.omms.central.graphics

import net.zhuruoling.omms.central.GlobalVariable
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.Dimension
import java.awt.event.MouseWheelEvent
import java.lang.management.ManagementFactory
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.math.max

fun Canvas.drawStringWithReturn(s: String, x: Float, y: Float, font: Font, paint: Paint) {
    if (s.contains("\n")) {
        val height = font.metrics.height
        var posY = y
        s.split("\n").forEach {
            this.drawString(it, x, posY, font, paint)
            posY += height
        }
    } else {
        this.drawString(s, x, y, font, paint)
    }
}

data class Box(val height: Float, val width: Float)

class SimpleGuiSkikoView : SkikoView {
    private var lastFrameTime = 1L
    private val font = Font()
    private val runtime = ManagementFactory.getRuntimeMXBean()
    private val fontHeight = font.metrics.height
    private val dx = 10f
    private val dy = 10f
    private val visibleLineCount = 30
    private val fontMaxWidth = font.metrics.maxCharWidth
    private val paint = Paint().apply {
        color = Color.WHITE
    }
    private var visibleLogs = CopyOnWriteArrayList<String>()
    private var scrolledLines = 0
    fun onMouseScroll(e: MouseWheelEvent) {
        val scroll = e.wheelRotation
        this.scrolledLines -= scroll
        if (scrolledLines >= GlobalVariable.logCache.size- visibleLineCount)
            scrolledLines = GlobalVariable.logCache.size - visibleLineCount
        if (scrolledLines <= 0) {
            scrolledLines = 0
        }
    }

    private fun getMemoryUsageString(): String {
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val heapMemoryUsage = memoryMXBean.heapMemoryUsage
        val nonHeapMemoryUsage = memoryMXBean.nonHeapMemoryUsage
        val maxMemory = (heapMemoryUsage.max + nonHeapMemoryUsage.max) / 1024.0 / 1024.0
        val usedMemory = (heapMemoryUsage.used + nonHeapMemoryUsage.used) / 1024.0 / 1024.0
        return String.format("%.3fMiB/%.3fMiB", usedMemory, maxMemory)
    }

    private fun drawBox(string: String, offsetX: Float, offsetY: Float, canvas: Canvas) {
        val box = measureString(string)
        canvas.drawLine(offsetX, offsetY, offsetX + box.width, offsetY, paint)
        canvas.drawLine(offsetX, offsetY, offsetX, offsetY + box.height, paint)
        canvas.drawLine(offsetX, offsetY + box.height, offsetX + box.width, offsetY + box.height, paint)
        canvas.drawLine(offsetX + box.width, offsetY + box.height, offsetX + box.width, offsetY, paint)
    }

    private fun measureString(s: String): Box {
        return if (s.contains("\n")) {
            val l = s.split("\n");
            val height = (l.size) * fontHeight
            var width = 0f
            l.forEach {
                width = max(it.count() * fontMaxWidth, width)
            }
            Box(height + 1, width + 1)
        } else {
            Box(fontHeight + 1, s.length * fontMaxWidth + 1)
        }
    }


    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val totalCount = GlobalVariable.logCache.size
        var l = totalCount - scrolledLines - visibleLineCount
        var r = totalCount - scrolledLines
        l = if (l <= 0) 0 else l
        r = if (r >= totalCount) totalCount else r
        visibleLogs.clear()
        visibleLogs += GlobalVariable.logCache.subList(l, r)
        canvas.clear(Color.BLACK)
        val frameTime = ((nanoTime - lastFrameTime) / 1e6)
        val upTime = runtime.uptime / 1000.0
        val infoString =
            "FPS: ${1000 / frameTime}\nFrameTime: ${frameTime}ms\nUptime: $upTime\nMemory: ${getMemoryUsageString()}"
        canvas.drawStringWithReturn(infoString, 10F, fontHeight + 10, Font(), paint)
        var logOffsetY = measureString(infoString).height + dy + fontHeight + 10
        visibleLogs.forEach {
            canvas.drawString(it.replace("\t", "    "), dx, logOffsetY, font, paint)
            logOffsetY += fontHeight
        }
        lastFrameTime = nanoTime
    }

}

lateinit var window:JFrame

fun guiMain() {
    val skiaLayer = SkiaLayer()
    val view = SimpleGuiSkikoView()
    skiaLayer.addMouseWheelListener {
        view.onMouseScroll(it)
    }
    skiaLayer.skikoView = GenericSkikoView(skiaLayer, view)
    SwingUtilities.invokeLater {
        window = JFrame("Skiko example").apply {
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            preferredSize = Dimension(1280, 720)
        }
        skiaLayer.attachTo(window.contentPane)
        skiaLayer.needRedraw()
        window.pack()
        window.isVisible = true
    }
}