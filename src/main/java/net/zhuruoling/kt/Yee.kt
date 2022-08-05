package net.zhuruoling.kt

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.atomic.AtomicInteger

fun test(path: String): Int {
    val count = AtomicInteger()
    val file = File(path)
    try {
        BufferedReader(FileReader(file)).use { reader ->
            reader.lines().forEach {
                for (c in it.toCharArray()) {
                    if (c == ' ') {
                        count.getAndIncrement()
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return count.get()
}

class Files(files: ArrayList<File>) {
    var files: ArrayList<File> = ArrayList()
    init {
        this.files = files
    }
    override fun toString(): String {
        if (!files.isEmpty()) run {
            val builder: StringBuilder = java.lang.StringBuilder()
            files.forEach {
                builder.append(it.absolutePath).append(',')
            }
            return "Files(files=${builder.toString()})"
        }
        return "Files(empty)"
    }


    operator fun plus(left: Files): Files{
        val f: ArrayList<File> = files
        f.addAll(left.files)
        return Files(f)
    }

    operator fun plus(left: File): Files{
        val f = files
        f.add(left)
        return Files(f)
    }

    operator fun contains(left: File): Boolean{
        return this.files.contains(left)
    }
}