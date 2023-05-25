package net.zhuruoling.omms.central.foo

import com.google.gson.GsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    val proj = Project {
        id = "example"
        version = "0.0.1"
        build {
            source("src/main.kt")
            source("src/hello.kt")
        }
        dependencies {
            dependency("com.mojang:brigadier:1.0.18")
            dependency("org.slf4j:slf4j-api:1.7.36")
            dependency("ch.qos.logback:logback-classic:1.2.11")
            dependency("ch.qos.logback:logback-core:1.2.11")
        }
    }
    val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()


}
