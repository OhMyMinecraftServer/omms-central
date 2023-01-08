package net.zhuruoling.foo

import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

object Foo {
    fun bar() {
        //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        val logger = LoggerFactory.getLogger("YEE")
        val os = ManagementFactory.getOperatingSystemMXBean()
        val runtime = ManagementFactory.getRuntimeMXBean()
        logger.info(
            String.format(
                "${Util.PRODUCT_NAME} is running on %s %s %s at pid %d",
                os.name,
                os.arch,
                os.version,
                runtime.pid
            )
        )
    }
}

class Invokable(private val func: () -> Unit) {
    operator fun invoke() {
        func()
    }
}

fun run() {
    val func = Invokable {
        println("wdnmd")
    }

    func()


    val anotherFunc = {
        println("wdnmd too")
    }

    anotherFunc()

    println("hello")
}




