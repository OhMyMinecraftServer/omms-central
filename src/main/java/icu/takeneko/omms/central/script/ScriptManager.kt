package icu.takeneko.omms.central.script

import cn.hutool.core.thread.ThreadFactoryBuilder
import icu.takeneko.omms.central.RunConfiguration
import icu.takeneko.omms.central.foundation.FeatureOption
import icu.takeneko.omms.central.foundation.Manager
import icu.takeneko.omms.central.util.Util
import jep.Interpreter
import jep.SharedInterpreter
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ScriptManager : Manager(), AutoCloseable {

    private lateinit var interpreter: Interpreter
    private val logger = LoggerFactory.getLogger("ScriptManager")
    private var initialized = false
    private var scriptFileList = mutableListOf<Path>()
    private var executor = Executors.newSingleThreadExecutor(
        ThreadFactoryBuilder().setNamePrefix("PythonExecutor-").build()
    )
    private val scripts = mutableMapOf<Path, ScriptInstance>()

    override fun init() {
        if (!FeatureOption["script"]) return
        run {
            try {
                interpreter = SharedInterpreter().apply {
                    val bridge = Thread.currentThread().contextClassLoader.getResourceAsStream("bridge.py")
                        ?: throw RuntimeException("Python bridge not found.")
                    bridge.use { exec(it.reader().readText()) }
                    invoke("setup")
                }
                initialized = true
                logger.info("Initialized python interpreter.")
            } catch (e: Exception) {
                logger.error("Initialize python interpreter failed, script will be unavailable.", e)
            }
            Files.list(Util.absolutePath("scripts"))
                .filter { it.toFile().extension == "py" }.forEach {
                    scriptFileList.add(it)
                    scripts[it] = ScriptInstance(it, interpreter)
                }

        }
    }

    fun onUnload() {
        run {
            scripts.values.forEach {
                it.onUnload()
            }
        }
    }

    fun onLoad() {
        run {
            for (value in scripts.values) {
                value.load()
            }
        }
    }

    fun unload(script: String) {
        run {
            logger.info("Unloaded script $script")
        }
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
        if (!FeatureOption["script"]) return
        executor.submit {
            try {
                runnable()
            } catch (e: Exception) {
                logger.error("Run Task failed.", e)
            }
        }
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
    ScriptManager.onLoad()
    Thread.sleep(1000)
    ScriptManager.close()
}