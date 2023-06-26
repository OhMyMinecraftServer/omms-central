package net.zhuruoling.omms.central.foo

import com.google.gson.GsonBuilder
import io.ktor.util.*
import net.zhuruoling.omms.central.file.FileSystemDescriptor
import net.zhuruoling.omms.central.file.FileUtils
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.util.printRuntimeEnv
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileDescriptor
import java.util.*

val logger: Logger = LoggerFactory.getLogger("Test")

class BuildConfig {
    var sourcesPath: List<SourcesPath> = mutableListOf()

    class SourcesPath(val path: String)
}

fun BuildConfig.source(path: String) {
    this.sourcesPath += BuildConfig.SourcesPath(path)
}


class Dependencies {
    var dependencies: List<ProjectDependency> = mutableListOf()

    class ProjectDependency(val name: String)
}

fun Dependencies.dependency(id: String) {
    this.dependencies += Dependencies.ProjectDependency(name = id)
}


class Project {
    var id = ""
    var version = ""
    var buildConfig: BuildConfig = BuildConfig()
    var dependencies = Dependencies()
}

inline fun Project(func: Project.() -> Unit): Project {
    return Project().run { func(this);this }
}

inline fun Project.build(func: BuildConfig.() -> Unit) {
    this.buildConfig = BuildConfig().run { func(this);this }
}

inline fun Project.dependencies(func: Dependencies.() -> Unit) {
    this.dependencies = Dependencies().run { func(this);this }
}

fun main() {
    val list = mutableListOf<FileSystemDescriptor>()
    FileUtils.linuxListFileSystemDescriptorImpl(list)
    list.forEach(::println)
    println()
    FileUtils.getAllFileSystemDescriptors().forEach(::println)
}
