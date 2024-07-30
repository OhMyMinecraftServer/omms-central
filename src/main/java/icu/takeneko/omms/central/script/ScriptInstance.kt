package icu.takeneko.omms.central.script

import jep.Interpreter
import jep.python.PyObject
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.properties.Delegates

class ScriptInstance(private val scriptFile: Path, private val interpreter: Interpreter) {
    var moduleName: String? = null
        private set
    var scriptId: String? = null
        private set
    var state: ScriptState = ScriptState.REMOVE
        private set
    private var module: PyObject? = null
    private val logger = LoggerFactory.getLogger("ScriptInstance")
    private val serverInterface by Delegates.notNull<ServerInterface>()


    private fun importModule() {
        moduleName = "py_script$${scriptFile.name}"
        module = interpreter.invoke("import_file", moduleName, scriptFile) as PyObject
        scriptId = try {
            module!!.getAttr("__script_id__", String::class.java)
        } catch (e: Exception) {
            moduleName!!.removeSuffix(".py").replace("$", "_").replace(".", "_")
                .also { logger.error("Script $scriptFile does not define __script_id__, defaulting to $it", e) }
        }
        state = ScriptState.LOAD
    }

    private fun invokeEntrypoint(name: String) {
        try {
            interpreter.invoke(
                "invoke_entrypoint", mapOf(
                    "module" to module!!, "name" to name, "kwargs" to mapOf(
                        "server" to serverInterface
                    )
                )
            )
        } catch (e: Exception) {
            logger.error("invokeEntrypoint failed: ", e)
        }
    }

    fun assertState(vararg states: ScriptState) {
        if (state !in states) {
            throw IllegalStateException("Assert state failed, expected: ${states.joinToString(", ")}, actual: $state")
        }
    }

    fun load() {
        importModule()
        state = ScriptState.LOAD
    }

    fun onLoad() {
        assertState(ScriptState.LOAD, ScriptState.RELOAD)
        invokeEntrypoint("on_load")
        state = ScriptState.READY
    }

    fun onUnload() {
        assertState(ScriptState.READY)
        invokeEntrypoint("on_unload")
        state = ScriptState.UNLOAD
    }

    fun reload() {
        assertState(ScriptState.READY)
        invokeEntrypoint("on_unload")
        state = ScriptState.RELOAD
        load()
        onLoad()
    }

}