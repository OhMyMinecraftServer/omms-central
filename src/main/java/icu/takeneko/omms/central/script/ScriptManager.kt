package icu.takeneko.omms.central.script

import cn.hutool.core.thread.ThreadFactoryBuilder
import icu.takeneko.omms.central.RunConfiguration
import icu.takeneko.omms.central.fundation.Manager
import icu.takeneko.omms.central.util.Util
import jep.Interpreter
import jep.SharedInterpreter
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolutePathString

object ScriptManager : Manager(), AutoCloseable {

    private lateinit var interpreter: Interpreter
    private val logger = LoggerFactory.getLogger("ScriptManager")
    private var initialized = false
    private var scriptFileList = mutableListOf<String>()
    private var executor = Executors.newSingleThreadExecutor(
        ThreadFactoryBuilder()
            .setNamePrefix("PythonExecutor-")
            .build()
    )

    override fun init() {
        if (RunConfiguration.noScripts) {
            logger.warn("--noscripts has been set, ${Util.PRODUCT_NAME} won`t load any scripts")
            return
        }
        run {
            try {
                Files.list(Util.absolutePath("scripts"))
                    .filter { it.toFile().extension == "py" }.forEach {
                        scriptFileList += it.absolutePathString()
                    }
                interpreter = SharedInterpreter().apply {
                    exec("import sys")
                    exec("import os")
                    exec("sys.path.append('.')")
                    exec("from org.slf4j import LoggerFactory as lf")
                    exec("logger = lf.getLogger('Python')")
                    exec("cwd = os.getcwd()")
                }
                initialized = true
                pythonLogDebug("'sys.path = ' + str(sys.path)")
                pythonLogDebug("'os.getcwd() = ' + str(cwd)")
                logger.info("Initialized python interpreter.")
            } catch (e: Exception) {
                logger.error("Initialize python interpreter failed, script will be unavailable.", e)
            }
        }
    }

    private fun pythonLogInfo(expression: String) {
        exec("logger.info($expression)")
    }

    private fun pythonLogDebug(expression: String) {
        exec("logger.debug($expression)")
    }

    private fun checkInitialized() {
        if (!initialized) {
            throw IllegalStateException("Not Initialized")
        }
    }

    fun exec(statement: String) {
        run {
            checkInitialized()
            interpreter.exec(statement)
        }
    }

    fun import(what: String) {
        exec("import $what")
    }

    fun importFrom(pkg: String, what: String, asSome: String? = null) {
        exec("from $pkg import $what${if (asSome != null) " as $asSome" else ""}")
    }

    fun run(runnable: () -> Unit) {
        executor.submit(runnable)
    }

    override fun close() {
        run {
            if (!initialized) return@run
            interpreter.close()
        }
        executor.shutdown()
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS)
    }
}

fun main() {
    ScriptManager.init()
    Thread.sleep(1000)
    ScriptManager.close()
}