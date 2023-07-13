package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Test")
fun main() {
    repeat(10){ println(Util.randomStringGen(32)) }
}

