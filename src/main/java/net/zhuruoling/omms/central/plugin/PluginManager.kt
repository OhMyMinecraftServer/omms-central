package net.zhuruoling.omms.central.plugin

import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency
import net.zhuruoling.omms.central.plugin.exception.PluginException
import net.zhuruoling.omms.central.plugin.metadata.PluginDependencyRequirement
import net.zhuruoling.omms.central.plugin.metadata.PluginMetadata
import net.zhuruoling.omms.central.util.BuildProperties
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.module.ModuleDescriptor
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

object PluginManager : Manager(), Iterable<PluginInstance>{
    private var pluginMap = LinkedHashMap<String, PluginInstance>()
    private var pluginFileList = arrayListOf<String>()
    private lateinit var classLoader: URLClassLoader
    private val logger = LoggerFactory.getLogger("PluginManager")
    override fun init() {
        if (GlobalVariable.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginMap.clear()
        pluginFileList.clear()
        Files.list(Path.of(Util.joinFilePaths("plugins")))
            .filter { it.toFile().extension == "jar" }.forEach {
                pluginFileList += it.absolutePathString()
            }
        classLoader = URLClassLoader(pluginFileList.stream().map { File(it).toURI().toURL() }.toList().toTypedArray())
        pluginFileList.forEach {
            PluginInstance(classLoader, Path(it)).run {
                loadJar()
                if (pluginState != PluginState.PRE_LOAD) return@forEach
                this
            }.run {
                if (pluginMetadata.id in pluginMap) {
                    logger.error("Plugin $it has a same id with plugin ${pluginMap[pluginMetadata.id]!!.pluginPathUrl.path}")
                    return@forEach
                }
                loadPluginClasses()
                if (pluginState == PluginState.ERROR) return@forEach
                this.pluginMetadata.id to this
            }.apply { pluginMap += this }
        }
    }

    fun loadAll() {
        checkRequirements()
        pluginMap.forEach {
            logger.debug("Loading plugin ${it.key}")
            it.value.onInitialize()
        }
    }

    operator fun get(id: String): PluginInstance? {
        return pluginMap[id]
    }
    private fun checkRequirements() {
        val dependencies = mutableListOf<PluginDependency>()
        dependencies += PluginDependency(
            ModuleDescriptor.Version.parse(BuildProperties["version"]!!),
            BuildProperties["applicationName"]!!
        )
        pluginMap.forEach {
            dependencies += PluginDependency(ModuleDescriptor.Version.parse(it.value.pluginMetadata.version), it.key)
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

