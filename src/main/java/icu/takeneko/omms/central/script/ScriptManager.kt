package icu.takeneko.omms.central.script

import cn.hutool.core.thread.ThreadFactoryBuilder
import icu.takeneko.omms.central.RunConfiguration
import icu.takeneko.omms.central.fundation.Manager
import icu.takeneko.omms.central.util.Util
import jep.Interpreter
import jep.JepException
import jep.SharedInterpreter
import jep.python.PyObject
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

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
    private val modules = mutableMapOf<String, PyObject>()
    private val scripts = mutableMapOf<String, PyObject>()

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
            interpreter.apply {
                for (s in scriptFileList) {
                    val moduleName = "py_script$${Path(s).name}"
                    val result = invoke("import_file", moduleName, s) as PyObject
                    modules += moduleName to result
                    val scriptId = try {
                        result.getAttr("__script_id__", String::class.java)
                    } catch (e: Exception) {
                        moduleName.removeSuffix(".py").replace("$", "_").replace(".", "_")
                            .also { logger.error("Script $s does not define __script_id__, defaulting to $it", e) }
                    }
                    scripts += scriptId to result
                }
            }
            logger.info("Initialized python scripts.")
        }
    }

    private fun pythonLogInfo(expression: String) {
        exec("logger.info($expression)")
    }

    private fun pythonLogDebug(expression: String) {
        exec("logger.debug($expression)")
    }

    fun onLoad() {
        run {
            scripts.keys.forEach {
                load(it)
            }
        }
    }

    fun load(script: String) {
        val obj = scripts[script] ?: throw IllegalArgumentException("Script $script does not exist")
        try {
            interpreter.invoke(
                "invoke_entrypoint", mapOf(
                    "module" to obj,
                    "name" to "on_load",
                    "kwargs" to mapOf(
                        "server" to ServerInterface(script)
                    )
                )
            )
        } catch (e: JepException) {
            logger.error("Load script $script failed.", e)
        }
        logger.info("Loaded script $script")
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
        executor.submit {
            try {
                runnable()
            }catch (e:Exception){
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