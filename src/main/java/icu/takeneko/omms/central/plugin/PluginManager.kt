package icu.takeneko.omms.central.plugin

import icu.takeneko.omms.central.GlobalVariable
import icu.takeneko.omms.central.plugin.depedency.PluginDependency
import icu.takeneko.omms.central.plugin.exception.PluginException
import icu.takeneko.omms.central.plugin.metadata.PluginDependencyRequirement
import icu.takeneko.omms.central.plugin.metadata.PluginMetadata
import icu.takeneko.omms.central.util.BuildProperties
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.module.ModuleDescriptor
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

object PluginManager : Manager(), Iterable<PluginInstance> {
    private var pluginMap = LinkedHashMap<String, PluginInstance>()
    private var pluginFileList = arrayListOf<String>()
    private lateinit var classLoader: JarClassLoader
    private val logger = LoggerFactory.getLogger("PluginManager")
    override fun init() {
        if (GlobalVariable.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginMap.clear()
        pluginFileList.clear()
        Files.list(Util.absolutePath("plugins"))
            .filter { it.toFile().extension == "jar" }.forEach {
                pluginFileList += it.absolutePathString()
            }
        classLoader = JarClassLoader(this::class.java.classLoader).apply {
            pluginFileList.map { File(it) }.forEach(this::loadJar)
        }
        pluginFileList.forEach {
            loadPluginFromFile(Path(it))?.apply { pluginMap += this }
        }
        Thread.currentThread().contextClassLoader = classLoader
    }

    private fun loadPluginFromFile(it: Path): Pair<String, PluginInstance>? {
        return PluginInstance(classLoader, it).run {
            loadJar()
            if (pluginState != PluginState.PRE_LOAD) return null
            this
        }.run {
            if (pluginMetadata.id in pluginMap) {
                logger.error("Plugin $it has a same id with plugin ${pluginMap[pluginMetadata.id]!!.pluginPathUrl.path}")
                return null
            }
            loadPluginClasses()
            if (pluginState == PluginState.ERROR) return null
            this.pluginMetadata.id to this
        }
    }

    fun loadAll() {
        checkRequirements()
        pluginMap.forEach {
            logger.debug("Loading plugin ${it.key}")
            it.value.onInitialize()
        }
    }

    fun refreshPlugins() {
        val beforeFiles = ArrayList(pluginFileList)
        val afterFiles = buildList {
            Files.list(Util.absolutePath("plugins"))
                .filter { it.toFile().extension == "jar" }.forEach {
                    this += it.absolutePathString()
                }
        }
        beforeFiles.forEach {
            if (it !in afterFiles) throw UnsupportedOperationException("Cannot remove plugins.")
        }
        pluginFileList.clear()
        pluginFileList += afterFiles
        val newFiles = (afterFiles - beforeFiles.toSet()).map { File(it) }
        newFiles.forEach(this.classLoader::loadJar)
        newFiles.forEach {
            logger.info("Loading plugin from $it")
            loadPluginFromFile(it.toPath())?.apply {
                pluginMap += this
                checkRequirements()
                logger.info("Initializing plugin ${this.second.pluginMetadata.id}")
                this.second.onInitialize()
            }
        }
    }

    operator fun get(id: String): PluginInstance? {
        return pluginMap[id]
    }

    fun reloadAll() {
        synchronized(pluginMap) {
            pluginMap.values.forEach {
                logger.debug("preOnReload ${it.pluginMetadata.id}")
                it.pluginMain.preOnReload()
            }
            classLoader.reloadAllClasses()
            pluginMap.values.forEach {
                logger.debug("postOnReload ${it.pluginMetadata.id}")
                it.pluginMain.postOnReload()
            }
        }
    }

    private fun checkRequirements() {
        val dependencies = mutableListOf<PluginDependency>()
        dependencies += PluginDependency(
            ModuleDescriptor.Version.parse(BuildProperties["version"]!!),
            BuildProperties["applicationName"]!!
        )
        pluginMap.forEach {
            dependencies += PluginDependency(
                ModuleDescriptor.Version.parse(it.value.pluginMetadata.version),
                it.key
            )
        }
        val unsatisfied = mutableMapOf<PluginMetadata, List<PluginDependencyRequirement>>()
        pluginMap.forEach {
            unsatisfied += it.value.pluginMetadata to it.value.checkPluginDependcencyRequirements(dependencies)
        }
        if (unsatisfied.any { it.value.isNotEmpty() }) {
            println("not empty")
            val dependencyMap = mutableMapOf<String, String>()
            dependencies.forEach {
                dependencyMap += it.id to it.version.toString()
            }
            val builder = StringBuilder()
            builder.append("Incompatible plugin set.\n")
            builder.append("Unmet dependency listing:\n")
            unsatisfied.forEach {
                it.value.forEach { requirement ->
                    builder.append(
                        "\t${it.key.id} ${it.key.version} requires ${requirement.id} of version ${requirement.requirement}, ${if (requirement.id !in dependencyMap) "which is missing!" else "but only the wrong version are present: ${dependencyMap[requirement.id]}!"}\n"
                    )
                }
            }
            builder.append("A potential solution has been determined:\n")
            unsatisfied.forEach { entry ->
                entry.value.forEach {
                    builder.append(
                        if (it.id !in dependencyMap)
                            "\tInstall ${it.id} of version ${it.requirement}."
                        else
                            "\tReplace ${it.id} ${dependencyMap[it.id]} with ${it.id} of version ${it.requirement}"
                    )
                    builder.append("\n")
                }
            }
            throw PluginException(builder.toString())
        }
    }

    override fun iterator(): Iterator<PluginInstance> {
        return pluginMap.values.iterator()
    }
}

