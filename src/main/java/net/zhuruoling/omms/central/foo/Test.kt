package net.zhuruoling.omms.central.foo

import cn.hutool.core.exceptions.ExceptionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
val logger: Logger = LoggerFactory.getLogger("Test")
fun main() {
    try {
        throw Exception()
    }catch (e:Exception){
        println(ExceptionUtil.stacktraceToString(e))
    }
}

