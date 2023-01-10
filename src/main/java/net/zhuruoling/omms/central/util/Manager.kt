package net.zhuruoling.omms.central.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class Manager<K, V> {

    private val table = hashMapOf<K,V>()

    fun init(dataFolder: String, extName: String) {
        table.clear()
        val folder = File(Util.joinFilePaths(dataFolder))
        val files = mutableListOf<Path>()
        Files.list(folder.toPath()).forEach {
            val file = it.toFile()
            if (file.extension == extName){
                files.add(it)
            }
        }
        files.forEach {

        }
    }

    fun get(k: K): V?{
        return table[k]
    }
}