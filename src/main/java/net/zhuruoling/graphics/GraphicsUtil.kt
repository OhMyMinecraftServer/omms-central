package net.zhuruoling.graphics

import io.ktor.util.*
import net.zhuruoling.console.ConsoleCommandHandler
import net.zhuruoling.system.SystemUtil
import net.zhuruoling.util.Util
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.image.BufferedImage
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

fun getGraphics(bufferedImage: BufferedImage, action: (Graphics2D) -> Unit) {
    val graphics2D = bufferedImage.createGraphics()
    action.invoke(graphics2D)
    graphics2D.dispose()
}

fun clearImage(bufferedImage: BufferedImage, color: Color = Color.WHITE) {
    getGraphics(bufferedImage) {
        it.color = color
        it.stroke = BasicStroke(1f)
        it.fillRect(0, 0, bufferedImage.width, bufferedImage.height)
    }
}

fun test(len: Int) {
    println("Creating image")
    val image = createImage(1080, 1720)
    clearImage(image)
    getGraphics(image) {
        it.font = Font("Consolas", Font.PLAIN, 40)
        it.color = Color.BLACK
        val h = it.fontMetrics.height
        for (i in 1..len) {
            it.drawString(Util.randomStringGen(i), 0, (i) * h)
        }
    }
    saveImage(Path(Util.joinFilePaths("image", "${Util.randomStringGen(8)}.png")), image)
}

fun info() {
    println("Creating image")
    val info = SystemUtil.getSystemInfo()

    val strings = mutableListOf<String>()
    strings.add("Operating system:${info.osName} ${info.osVersion} ${info.osArch}")
    strings.add("Processor: ${info.processorInfo.processorName.trimEnd()} x${info.processorInfo.physicalCPUCount}")
    val runtime = ManagementFactory.getRuntimeMXBean()
    val upTime = runtime.uptime / 1000.0
    strings.add("Uptime: %.3fS".formatted(upTime))
    val memoryMXBean = ManagementFactory.getMemoryMXBean()
    val heapMemoryUsage = memoryMXBean.heapMemoryUsage
    val nonHeapMemoryUsage = memoryMXBean.nonHeapMemoryUsage
    val maxMemory = (heapMemoryUsage.max + nonHeapMemoryUsage.max) / 1024.0 / 1024.0
    val usedMemory = (heapMemoryUsage.used + nonHeapMemoryUsage.used) / 1024.0 / 1024.0
    strings.add("JVM Memory usage: %.3fMiB/%.3fMiB".formatted(usedMemory, maxMemory))
    strings.add(
        "RAM: ${info.memoryInfo.memoryUsed / 1024 / 1024}MB/${info.memoryInfo.memoryTotal / 1024 / 1024}MB(${
            String.format(
                "%.3f",
                1.0 - info.memoryInfo.memoryUsed * 1.0f / info.memoryInfo.memoryTotal
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
            ) {
                if (it.displayName.contains("PCIe")
                    || it.displayName.toLowerCasePreservingASCIIRules().contains("wi")
                    || it.displayName.toLowerCasePreservingASCIIRules().contains("fi")
                ) else {
                    return@forEach
                }
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
    val font = Font("Consolas", Font.PLAIN, 32)
    val image = createImage(
        1080,
        (font.getLineMetrics(
            "wy0OaabbcGg",
            createImage(600, 800).createGraphics().fontRenderContext
        ).height * strings.size).plus(64).toInt()
    )
    clearImage(image)
    getGraphics(image) { graphics2D ->
        graphics2D.font = font
        graphics2D.color = Color.BLACK
        val h = graphics2D.fontMetrics.height
        for (i in 0 until strings.size) {
            graphics2D.drawString(strings[i], 0, (i + 1) * h)
        }
    }
    saveImage(Path(Util.joinFilePaths("image", "system_info.png")), image)
}