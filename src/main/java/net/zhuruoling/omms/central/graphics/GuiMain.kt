package net.zhuruoling.omms.central.graphics

import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.command.CommandManager
import net.zhuruoling.omms.central.command.CommandSourceStack
import net.zhuruoling.omms.central.main.CentralServer
import net.zhuruoling.omms.central.system.OperatingSystem
import net.zhuruoling.omms.central.util.BuildProperties
import net.zhuruoling.omms.central.util.Util
import org.jetbrains.skia.*
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseWheelEvent
import java.lang.management.ManagementFactory
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.math.max
import kotlin.math.roundToInt

private val logger = LoggerFactory.getLogger("GuiMain")

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

data class Rectangle(val height: Float, val width: Float)

class SimpleGuiSkikoView : SkikoView {
    private var lastFrameTime = 1L
    private val font: Font = Font(Typeface.makeFromName(GlobalVariable.consoleFont, FontStyle.NORMAL), 14.0f)
    private val runtime = ManagementFactory.getRuntimeMXBean()
    private val linePadding = 4f
    private val fontHeightWithPadding = font.measureText("[(1919810g]}").height + linePadding
    private val dx = 10f
    private val dy = 10f
    private val visibleLineCount = 30
    private val textFieldPadding = 5f
    private val fontWidth = font.measureTextWidth("114514") / 6
    private val paint = Paint().apply {
        color = Color.WHITE
        this.isAntiAlias = true
    }
    private val backgroundPaint = Paint().apply {
        color = Color.makeARGB(255, 200, 200, 200)
        this.isAntiAlias = true
    }
    private val foregroundPaint = Paint().apply {
        color = Color.makeARGB(255, 0, 0, 0)
        this.isAntiAlias = true
    }
    private var visibleLogs = CopyOnWriteArrayList<String>()
    private var scrolledLines = 0
    private var cursorPos = 0
    private var textFieldString = ""
    private val maxCharCount = 110
    private val textFieldTextOffsetX = dx + textFieldPadding
    private val textFieldTextOffsetY = fontHeightWithPadding * (visibleLineCount + 3) + fontHeightWithPadding * 4 + dy
    private val textFieldWidth = fontWidth * (maxCharCount - 1)
    private val textFieldHeight = fontHeightWithPadding + textFieldPadding * 2
    private val textFieldOffsetX = textFieldTextOffsetX - textFieldPadding
    private val textFieldOffsetY = textFieldTextOffsetY - fontHeightWithPadding
    private val cursorThickness = 2f

    fun onMouseScroll(e: MouseWheelEvent) {
        val scroll = e.wheelRotation
        this.scrolledLines -= if (e.isControlDown) scroll * 3 else scroll
        if (scrolledLines >= GlobalVariable.logCache.size - visibleLineCount)
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

    private fun measureString(s: String): Rectangle {
        return if (s.contains("\n")) {
            val l = s.split("\n");
            val height = (l.size) * fontHeightWithPadding
            var width = 0f
            l.forEach {
                width = max(it.count() * fontWidth, width)
            }
            Rectangle(height + 1, width + 1)
        } else {
            Rectangle(fontHeightWithPadding + 1, s.length * fontWidth + 1)
        }
    }

    fun scrollToEnd(){
        scrolledLines = 0
    }


    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        try {
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
                "FPS: ${String.format("%.2f",1000 / frameTime)}\nFrameTime: ${String.format("%.3f",frameTime)}ms\nUptime: $upTime\nMemory: ${getMemoryUsageString()}"
            //render status and logs
            canvas.drawRect(Rect(dx, dy - 3, dx + 40 * fontWidth, dy + 4 * fontHeightWithPadding + 5), backgroundPaint)
            canvas.drawStringWithReturn(infoString, dx + fontWidth, fontHeightWithPadding + dy - 3, font, foregroundPaint)
            var logOffsetY = measureString(infoString).height + dy + fontHeightWithPadding + 10
            visibleLogs.forEach {
                canvas.drawString(it.replace("\t", "    "), dx, logOffsetY, font, paint)
                logOffsetY += fontHeightWithPadding
            }

            //render textField
            canvas.drawRect(
                Rect(
                    textFieldOffsetX,
                    textFieldOffsetY,
                    textFieldOffsetX + textFieldWidth,
                    textFieldOffsetY + textFieldHeight
                ),
                backgroundPaint
            )
            canvas.drawString("> $textFieldString", textFieldTextOffsetX, textFieldTextOffsetY, font, foregroundPaint)
            val cursorL = (cursorPos + 2) * fontWidth + textFieldTextOffsetX
            val cursorR = (cursorPos + 3) * fontWidth + textFieldTextOffsetX
            for (i in 0 .. cursorThickness.roundToInt()){
                canvas.drawLine(
                    x0 = cursorL,
                    y0 = 2 + textFieldTextOffsetY + i,
                    x1 = cursorR,
                    y1 = 2 + textFieldTextOffsetY + i,
                    foregroundPaint
                )
            }
            //update frame time
            lastFrameTime = nanoTime
        } catch (e: Exception) {
            if (e !is ConcurrentModificationException)
                logger.error("Error occurred while rendering.", e)
        }
    }

    private fun processCursorMove(delta: Int) {
        cursorPos += delta
        if (cursorPos > textFieldString.length) cursorPos = textFieldString.length
        if (cursorPos <= 0) cursorPos = 0
        println("CursorPos = $cursorPos")
    }
    private fun processKeyType(char: Char?) {
        if (char == null) {
            if (textFieldString.isEmpty() or (cursorPos == 0)) return
            textFieldString = if (cursorPos == textFieldString.length)
                textFieldString.substring(0, textFieldString.length - 1)
            else
                textFieldString.substring(0, cursorPos - 1) + textFieldString.substring(cursorPos)
            cursorPos--
            return
        }
        if (textFieldString.length >= maxCharCount) return
        textFieldString = if (cursorPos == textFieldString.length)
            textFieldString + char
        else
            textFieldString.substring(0, cursorPos) + char + textFieldString.substring(cursorPos)
        cursorPos++
    }

    private fun processEnter() {
        cursorPos = 0
        println(textFieldString)
        var command = ""
        textFieldString.forEach {
            command += it
        }
        CentralServer.runOnMainThread {
            CommandManager.INSTANCE.reload()
            CommandManager.INSTANCE.dispatchCommand(
                command,
                CommandSourceStack(CommandSourceStack.Source.CONSOLE)
            )
        }
        textFieldString = String()
    }

    fun onKeyTyped(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_LEFT) {
            processCursorMove(-1)
            return
        }
        if (e.keyCode == KeyEvent.VK_RIGHT) {
            processCursorMove(1)
            return
        }
        if (e.keyCode == KeyEvent.VK_ENTER) {
            processEnter()
            return
        }
        if (e.keyCode == KeyEvent.VK_BACK_SPACE) {
            processKeyType(null)
            return
        }
        if (e.keyChar != KeyEvent.CHAR_UNDEFINED) {
            processKeyType(e.keyChar)
        }
    }

    private fun isClickInTextField(x: Int, y: Int) =
        (textFieldOffsetX <= x && x <= (textFieldOffsetX + textFieldWidth)) &&
                (textFieldOffsetY <= y && y <= (textFieldOffsetY + textFieldHeight))

    fun onMouseClicked(e: MouseEvent) {
        if (e.button == MouseEvent.BUTTON1) {
            val p = e.point
//            println("$textFieldOffsetX $textFieldOffsetY ${textFieldOffsetX + textFieldWidth} ${textFieldOffsetY + textFieldHeight}")
//            println("${isClickInTextField(p.x, p.y)} ${e.point}")
            if (isClickInTextField(p.x, p.y)) {
//                println("wdnmd")
            }
        }
    }
}

lateinit var window: JFrame
lateinit var view: SimpleGuiSkikoView
fun guiMain() {
    val skiaLayer = SkiaLayer()
    view = SimpleGuiSkikoView()
    skiaLayer.addMouseWheelListener {
        view.onMouseScroll(it)
    }
    skiaLayer.addMouseListener(object : MouseListener {
        override fun mouseClicked(e: MouseEvent) {
            view.onMouseClicked(e)
        }

        override fun mousePressed(e: MouseEvent) {}

        override fun mouseReleased(e: MouseEvent) {}

        override fun mouseEntered(e: MouseEvent) {}

        override fun mouseExited(e: MouseEvent) {}

    })
    skiaLayer.addKeyListener(object : KeyListener {
        override fun keyTyped(e: KeyEvent) {
            //view.onKeyTyped(e)
        }

        override fun keyPressed(e: KeyEvent) {
            view.onKeyTyped(e)
        }

        override fun keyReleased(e: KeyEvent) {

        }

    })
    skiaLayer.skikoView = GenericSkikoView(skiaLayer, view)
    SwingUtilities.invokeLater {
        window = JFrame("${Util.PRODUCT_NAME} ${BuildProperties["version"]}").apply {
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            preferredSize = Dimension( if (OperatingSystem.IS_MACOS) 1600 else 1280, if (OperatingSystem.IS_MACOS) 800 else 720)
        }
        skiaLayer.attachTo(window.contentPane)
        skiaLayer.needRedraw()
        window.pack()
        window.isVisible = true
        logger.debug("RenderInfo: ")
        skiaLayer.renderInfo.split("\n").forEach {
            if (it.isEmpty()) return@forEach
            logger.debug("\t - $it")
        }
    }
}
