package net.zhuruoling.omms.central.system.runner

import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util

object RunnerManager :Manager() {

    data class RunnerInfo(val id: String, val description: String, val command: String, val workingDir: String)

    private val runnerMap = mutableMapOf<String, RunnerDaemon>()

    fun createRunner(
        command: String, workingDir: String, description: String
    ): RunnerDaemon {
        return createRunner(command, workingDir, description, {})
    }

    fun createRunner(
        command: String, workingDir: String, description: String,
        onRunnerOutputLine: (String) -> Unit = {},
        processOutputLineFormatter: (String) -> String = { it },
        shouldRecordProcessOutputLine:(String) -> Boolean = {true}
    ): RunnerDaemon {
        val id = generateRunnerId()
        val runner = RunnerDaemon(id, command, workingDir, description, onRunnerOutputLine, processOutputLineFormatter, shouldRecordProcessOutputLine)
        runnerMap[id] = runner
        return runner
    }

    fun forEach(func: RunnerManager.(Map.Entry<String, RunnerDaemon>) -> Unit) {
        runnerMap.forEach {
            func.invoke(this, it)
        }
    }

    fun launchRunner(runnerId: String) {
        if (runnerMap.containsKey(runnerId)) {
            runnerMap[runnerId]!!.start()
        } else {
            throw IllegalArgumentException("Runner-$runnerId Not exist.")
        }
    }

    fun <T> runIfRunnerExists(runnerId: String, func: RunnerManager.(String, RunnerDaemon) -> T): T? {
        synchronized(runnerMap) {
            if (runnerMap.containsKey(runnerId)) {
                return func(this, runnerId, runnerMap[runnerId]!!)
            }
        }
        return null
    }

    private fun generateRunnerId() = Util.randomStringGen(8)

    fun runnerExists(id: String): Boolean = runnerMap.containsKey(id)

    fun getRunner(id: String): RunnerDaemon? = runnerMap[id]

    fun getAllRunnerInfo(): List<RunnerInfo> {
        val list = mutableListOf<RunnerInfo>()
        this.forEach {
            list.add(RunnerInfo(it.key, it.value.description, it.value.launchCommand, it.value.workingDir))
        }
        return list
    }

    override fun init() {

    }

}