package net.zhuruoling.omms.central.foo

import cn.hutool.core.io.file.FileSystemUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.FileSystems

val logger: Logger = LoggerFactory.getLogger("Test")


fun main() {
    FileSystems.getDefault().fileStores.forEach {

        println("$it type: ${it.type()}  name: ${it.name()}")
    }
    println(FileSystemUtil.getRoot(FileSystems.getDefault()))
}
