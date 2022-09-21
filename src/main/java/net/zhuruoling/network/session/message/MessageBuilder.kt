package net.zhuruoling.network.session.message

import com.google.gson.GsonBuilder
import org.jetbrains.annotations.NotNull
import net.zhuruoling.util.Result


@NotNull
fun build(result: Result): String? {
    val gson = GsonBuilder().serializeNulls().create()
    val message = Message(result.name, arrayOf())
    return gson.toJson(message)
}

@NotNull
fun  build(result: Result, load: Array<String?>?): String? {
    val gson = GsonBuilder().serializeNulls().create()
    return gson.toJson(Message(result.name, load))
}

@NotNull
fun  build(result: Result, load: List<String>): String? {
    val gson = GsonBuilder().serializeNulls().create()
    return gson.toJson(Message(result.name, load.toTypedArray()))
}

@NotNull
fun  build(result: Result, load: Set<String>): String? {
    val gson = GsonBuilder().serializeNulls().create()
    return gson.toJson(Message(result.name, load.toTypedArray()))
}

fun build(code: String, load: Array<String>): String {
    val gson = GsonBuilder().serializeNulls().create()
    return gson.toJson(Message(code, load))
}
