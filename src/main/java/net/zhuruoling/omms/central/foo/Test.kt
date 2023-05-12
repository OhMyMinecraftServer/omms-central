package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.plugin.PluginInstance
import net.zhuruoling.omms.central.security.CryptoUtil
import net.zhuruoling.omms.central.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

val logger: Logger = LoggerFactory.getLogger("Test")


fun main() {
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
//    PluginInstance(Util.joinFilePaths("plugins","jarTest-0.0.1.jar")).use {
//        it.run {
//            loadJar()
//            onLoad()
//            onUnload()
//        }
//    }
    val key = "ENCRYPT KEY"
    val src = "https://gfw.report/publications/usenixsecurity23/zh/"
    val encrypted = CryptoUtil.aesEncryptToB64String(src, key)
    println(Util.base64Encode(src))
    println(encrypted)
    println(CryptoUtil.aesDecryptFromB64String(encrypted, key))
    assert(CryptoUtil.aesDecryptFromB64String(CryptoUtil.aesEncryptToB64String(src, key), key) == src)
    val compressed = CryptoUtil.gzipCompress(encrypted)
    println(compressed)
    val decompressed = CryptoUtil.gzipDecompress(compressed)
    println(decompressed)
    assert(compressed == decompressed)
}
