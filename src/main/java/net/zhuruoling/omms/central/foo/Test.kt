package net.zhuruoling.omms.central.foo

import io.ktor.util.*
import net.zhuruoling.omms.central.plugin.JarClassLoader
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import oshi.SystemInfo
import java.nio.file.Path
import java.util.Base64
import java.util.regex.Pattern
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString

val logger: Logger = LoggerFactory.getLogger("Test")

object C

fun main() {
    val cl = JarClassLoader(C::class.java.classLoader)
    val jars = Path(Util.joinFilePaths("jars"))
        .listDirectoryEntries()
        .filter { it.pathString.endsWith(".jar") }
        .map(Path::toFile)
    println(jars)
    jars.forEach {
        cl.loadJar(it)
    }
    val className = "com.test.Test"
    val clazz = cl.loadClass(className)
    println(clazz)
    clazz.getDeclaredConstructor(java.lang.String::class.java).newInstance("wdnmd")
    println("replace jar plz")
    readln()
    cl.reloadClass(className)
    cl.reloadAllClasses()
    clazz.getDeclaredConstructor(java.lang.String::class.java).newInstance("wdnmd")
}


