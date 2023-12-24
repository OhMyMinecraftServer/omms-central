package net.zhuruoling.omms.central.plugin

import net.zhuruoling.omms.central.util.InstrumentationAccess
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.ZipFile

class JarClassLoader(parent: ClassLoader) : ClassLoader(parent) {
    private val jarFiles = mutableListOf<File>()
    private val jarEntries = mutableMapOf<String, File>()
    private val jarClassEntriesByClassName = mutableMapOf<String, Pair<String, File>>()
    private val loadedClassesFromJar = mutableMapOf<String, Class<*>>()
    private val classLoadingLock = Any()
    private val fileLoadingLock = Any()
    private val logger = LoggerFactory.getLogger("JarClassLoader")

    fun loadJar(file: File) {
        synchronized(fileLoadingLock) {
            synchronized(classLoadingLock) {
                logger.debug("Add jar({}) to classloader.", file)
                if (!file.exists()) throw FileNotFoundException(file.toString())
                jarFiles += file
                ZipFile(file).use {
                    for (entry in it.entries()) {
                        if (!entry.isDirectory) {
                            jarEntries += entry.name to file
                            if (entry.name.endsWith(".class")) {
                                jarClassEntriesByClassName[entry.name.replace("/", ".").removeSuffix(".class")] =
                                    entry.name to file
                                logger.debug("Found class {} in file {}", entry.name, file)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun tryLoadClassFromJar(name: String): Class<*>? {
        logger.debug("Loading class from jar $name")
        val (path, file) = jarClassEntriesByClassName[name] ?: return null
        return ZipFile(file).use {
            val entry = it.getEntry(path) ?: return null
            val stream = it.getInputStream(entry)
            val bytes = stream.readAllBytes()
            val clazz = defineClass(name, bytes, 0, bytes.size)
            loadedClassesFromJar[name] = clazz
            clazz
        }
    }

    @Synchronized
    fun reloadAllClasses() {
        val classes = mutableMapOf<File, MutableMap<String, String>>()
        loadedClassesFromJar.forEach {
            val (entryName, f) = jarClassEntriesByClassName[it.key]!!
            if (f !in classes) classes[f] = mutableMapOf()
            classes[f]!![it.key] = entryName
        }
        val classNeedReload = mutableListOf<String>()
        classes.forEach { (file, map) ->
            ZipFile(file).use {
                val entries = it.entries().toList().map { it.name }
                map.forEach { (className, entryName) ->
                    if (entryName in entries) {
                        classNeedReload += className
                        //throw IllegalStateException("Removal of class ($className).")
                    }
                }
            }
        }
        classNeedReload.forEach {
            reloadClass(it)
        }
    }

    fun reloadClass(name: String): Class<*> {
        logger.debug("Reloading class $name")
        synchronized(classLoadingLock) {
            if (name !in loadedClassesFromJar && name !in jarClassEntriesByClassName)
                throw IllegalStateException("class $name is not loaded from current JarClassLoader")
            if (name !in loadedClassesFromJar) {
                return loadClass(name, false)
            }
            InstrumentationAccess.instrumentation.apply {
                val tr = ReloadClassTransformer(this@JarClassLoader, name)
                addTransformer(tr, true)
                retransformClasses(loadedClassesFromJar[name])
                removeTransformer(tr)
            }
            return loadedClassesFromJar[name]!!
        }
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(classLoadingLock) {
            logger.debug("Loading class {}", name)
            var clazz = findLoadedClass(name)
            if (clazz != null) {
                logger.debug("class {} already loaded.", name)
                return clazz
            }
            clazz = tryLoadClassFromJar(name)
            if (clazz != null) {
                logger.debug("Found class {} from jar.", name)
                return clazz
            }
            return super.loadClass(name, resolve)
        }
    }

    override fun getResourceAsStream(name: String): InputStream? {
        synchronized(classLoadingLock) {
            if (name !in jarEntries) return null
            val bytes = (ZipFile(jarEntries[name]!!).use {
                it.getInputStream(it.getEntry(name)).readAllBytes()
            } ?: return super.getResourceAsStream(name))
            return ByteArrayInputStream(bytes)
        }
    }

    private fun getClassBytes(className: String): ByteArray {
        synchronized(fileLoadingLock) {
            if (className !in jarClassEntriesByClassName) throw IllegalArgumentException("class $className is not loaded from current JarClassLoader")
            val (path, file) = jarClassEntriesByClassName[className]!!
            val it = ZipFile(file)
            val entry = it.getEntry(path)
            val stream = it.getInputStream(entry)
            val bytes = stream.readAllBytes()
            it.close()
            return bytes
        }
    }

    class ReloadClassTransformer(
        private val jarClassLoader: JarClassLoader,
        private val className: String
    ) : ClassFileTransformer {
        private var reloaded = AtomicBoolean(false)
        override fun transform(
            loader: ClassLoader,
            className: String,
            classBeingRedefined: Class<*>,
            protectionDomain: ProtectionDomain,
            classfileBuffer: ByteArray
        ): ByteArray {
            val name = className.replace("\\", ".").replace("/", ".")
            if (this.className != name) return classfileBuffer
            if (reloaded.get()) return classfileBuffer
            reloaded.set(true)
            return jarClassLoader.getClassBytes(name)
        }
    }
}
