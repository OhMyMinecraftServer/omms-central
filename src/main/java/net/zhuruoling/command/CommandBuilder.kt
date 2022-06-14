package net.zhuruoling.command

import com.google.gson.Gson

fun buildFromJson(text: String?): Command? = Gson().fromJson(text, Command::class.java)
