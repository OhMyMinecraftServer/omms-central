package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.config.Configuration
import net.zhuruoling.omms.central.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Test")

fun main() {
    val clazz = Configuration::class.java
    println(clazz.name)
    clazz.declaredFields.forEach {
        println(it.name)
    }
}

