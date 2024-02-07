package icu.takeneko.omms.central.util

import java.util.ResourceBundle

object BuildProperties {
    val map = mutableMapOf<String, String>()

    init {
        val bundle = ResourceBundle.getBundle("build")
        for(key in bundle.keys){
            map += key to bundle.getString(key)
        }
    }

    operator fun get(key: String): String?{
        return map[key]
    }

    fun forEach(function: (Map.Entry<String, String>) -> Unit){
        map.forEach(function)
    }
}