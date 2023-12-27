@file:Suppress("UNUSED")
package net.zhuruoling.omms.central.graphics

import net.zhuruoling.omms.central.system.info.SystemInfoUtil
import net.zhuruoling.omms.central.util.Util
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.Path


fun createImage(width: Int, height: Int): BufferedImage {
    return BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
}

fun saveImage(path: Path, bufferedImage: BufferedImage) {
    ImageIO.write(bufferedImage, "png", path.toFile())
}

fun BufferedImage.withGraphics(action: (Graphics2D) -> Unit) {
    val graphics2D = this.createGraphics()
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    action.invoke(graphics2D)
    graphics2D.dispose()
}

fun clearImage(bufferedImage: BufferedImage, color: Color = Color.WHITE) {
    bufferedImage.withGraphics {
        it.color = color
        it.stroke = BasicStroke(1f)
        it.fillRect(0, 0, bufferedImage.width, bufferedImage.height)
    }
}

fun main() {
    //info()
    val img = createImage(1280, 720)
    img.withGraphics {
        //renderBottomCard(it)
    }
    saveImage(Path(Util.joinFilePaths("image", "jrrp.png")), img)
}

fun test(len: Int) {
    println("Creating image")
    val image = createImage(1080, 1720)
    clearImage(image)
    image.withGraphics {
        it.font = Font("Consolas", Font.PLAIN, 40)
        it.color = Color.BLACK
        val h = it.fontMetrics.height
        for (i in 1..len) {
            it.drawString(Util.generateRandomString(i), 0, (i) * h)
        }
    }
    saveImage(Path(Util.joinFilePaths("image", "${Util.generateRandomString(8)}.png")), image)
}

fun info() {
    println("Creating image")
    val info = SystemInfoUtil.getSystemInfo()
    val strings = mutableListOf<String>()
    strings.add("Operating system:${info.osName} ${info.osVersion} ${info.osArch}")
    strings.add("Processor: ${info.processorInfo.processorName.trimEnd()} x${info.processorInfo.physicalCPUCount}")
    val runtime = ManagementFactory.getRuntimeMXBean()
    val upTime = runtime.uptime / 1000.0
    strings.add(String.format("Uptime: %.3fS", upTime))
    val memoryMXBean = ManagementFactory.getMemoryMXBean()
    val heapMemoryUsage = memoryMXBean.heapMemoryUsage
    val nonHeapMemoryUsage = memoryMXBean.nonHeapMemoryUsage
    val maxMemory = (heapMemoryUsage.max + nonHeapMemoryUsage.max) / 1024.0 / 1024.0
    val usedMemory = (heapMemoryUsage.used + nonHeapMemoryUsage.used) / 1024.0 / 1024.0
    strings.add(String.format("JVM Memory usage: %.3fMiB/%.3fMiB",usedMemory, maxMemory))
    strings.add(
        "RAM: ${info.memoryInfo.memoryUsed / 1024 / 1024}MB/${info.memoryInfo.memoryTotal / 1024 / 1024}MB(${
            String.format(
                "%.3f",
                info.memoryInfo.memoryUsed * 1.0f / info.memoryInfo.memoryTotal
            )
        })"
    )
    strings.add("Disks:")
    info.storageInfo.storageList.forEach {
        strings.add("  ${it.model} size:${it.size / 1024 / 1024}MiB")
    }
    strings.add("Partitions:")
    info.fileSystemInfo.fileSystemList.forEach {
        strings.add(
            "  ${it.mountPoint} : ${it.fileSystemType} ${it.free / 1024 / 1024}MB/${it.total / 1024 / 1024}MB(${
                String.format(
                    "%.3f",
                    1.0 - it.free * 1.0 / it.total
                )
            })"
        )
    }
    strings.add("Network:")
    info.networkInfo.apply {
        strings.add("  Hostname: ${this.hostName}")
        strings.add("  Domain name: ${this.domainName}")

        strings.add("  ipv4 Gateway:${this.ipv4DefaultGateway}")
        strings.add("  ipv6 Gateway:${this.ipv6DefaultGateway}")
        strings.add("  DNS Servers:")
        dnsServers.forEach {
            strings.add("    $it")
        }
        strings.add("  Network IFs:")
        networkInterfaceList.forEach {
            if (it.speed / 1024 / 1024 < 10
                || it.displayName.contains("VMware")
                || it.displayName.contains("Hyper-V")
                || it.displayName.contains("Virtual")
                || it.displayName.contains("TAP")
            ) {
                return@forEach
            }
            strings.add("    ${it.displayName}")
            strings.add("      speed: ${it.speed / 1024 / 1024}Mbps")
            strings.add("      MAC addr: ${it.macAddress}")
            strings.add("      mtu: ${it.mtu}")
            strings.add("      ipv4 addr:")
            it.ipv4Address.forEach { addr ->
                strings.add("        $addr")
            }
            strings.add("      ipv6 addr:")
            it.ipv6Address.forEach { addr ->
                strings.add("        $addr")
            }
        }
    }
    val font = Font("微软雅黑", Font.PLAIN, 32)
    val image = createImage(
        1080,
        (font.getLineMetrics(
            "wy0OaabbcGg",
            createImage(600, 800).createGraphics().fontRenderContext
        ).height * strings.size).plus(64).toInt()
    )
    clearImage(image)
    val background = ImageIO.read(File(Util.joinFilePaths("image", "background.JPG")))
    val zoom = image.width.toDouble() / background.width
    image.withGraphics {
        it.drawImage(
            background,
            AffineTransformOp(AffineTransform.getScaleInstance(zoom, zoom), AffineTransformOp.TYPE_BICUBIC),
            0,
            0
        )
    }
    image.withGraphics {
        it.font = font
        it.color = Color.BLACK
        val h = it.fontMetrics.height
        for (i in 0 until strings.size) {
            it.drawString(" " + strings[i], 0, (i + 1) * h)
        }
    }
    saveImage(Path(Util.joinFilePaths("image", "system_info.png")), image)
}