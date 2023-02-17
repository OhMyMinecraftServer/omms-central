package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.system.runner.RunnerDaemon
import net.zhuruoling.omms.central.system.runner.RunnerManager
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import kotlin.system.exitProcess

fun main() {
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    var runner: RunnerDaemon? = null
    runner = RunnerManager.createRunner(
        "java -jar -Xmx4G fabric.jar",
        "C:\\Users\\jkl-9\\Desktop\\服务器\\server",
        "Minecraft Server",
        {
            println(it)
            if (it.contains("Done")) {
                runner!!.input("stop")
            }
        }
    )

    println(runner.runnerId)
    val id = runner.runnerId
    assert(runner == RunnerManager.getRunner(id))
    runner.start()
    runner.waitForStart()
    println("runner started.")
    println(runner.processStarted)
    println(runner.processAlive)
    println(runner)
    runner.waitForProcessStop()
    println(runner.returnCode)
}
