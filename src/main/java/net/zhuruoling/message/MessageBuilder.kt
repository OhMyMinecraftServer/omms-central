package net.zhuruoling.message

import com.google.gson.GsonBuilder
import net.zhuruoling.util.Result
import net.zhuruoling.whitelist.WhitelistResult
import org.jetbrains.annotations.NotNull

@NotNull
fun build(result: WhitelistResult): String? {
    val gson = GsonBuilder().serializeNulls().create()
    val message = Message(result.name, arrayOf())
    return gson.toJson(message)
}

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