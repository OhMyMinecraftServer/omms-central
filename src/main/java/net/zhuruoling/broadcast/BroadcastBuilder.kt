package net.zhuruoling.broadcast

import com.google.gson.GsonBuilder

fun buildFromJson(content: String?): Broadcast? = GsonBuilder().serializeNulls().create().fromJson(content, Broadcast::class.java)

fun buildToJson(broadcast: Broadcast?): String? = GsonBuilder().serializeNulls().create().toJson(broadcast, Broadcast::class.java)
