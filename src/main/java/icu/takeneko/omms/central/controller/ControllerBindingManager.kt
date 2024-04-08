package icu.takeneko.omms.central.controller

import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.Util
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

object ControllerBindingManager : Manager() {

    private val storage = mutableMapOf<String, ControllerBindingConfig>()

    fun bind(config: ControllerBindingConfig) {
        storage[config.bindId] = config
        ControllerManager.replaceController(BoundControllerImpl(config))
    }

    override fun init() {
        val filePath = Util.absolutePath("bindConfig.json").toFile()
        if (!filePath.exists()) {
            filePath.createNewFile()
            filePath.writer().use {
                it.append("{}")
            }
        }
        filePath.reader().use {
            val map = Util.gson.fromJson(it, storage::class.java)
            storage.clear()
            storage += map
        }
        storage.forEach {
            ControllerManager.replaceController(BoundControllerImpl(it.value))
        }
    }
}

inline fun controllerBinding(fn: ControllerBindingConfig.() -> Unit) = ControllerBindingConfig().apply(fn)

