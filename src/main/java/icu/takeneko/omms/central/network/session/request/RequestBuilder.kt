package icu.takeneko.omms.central.network.session.request

import com.google.gson.Gson

fun buildFromJson(text: String?): Request? = Gson().fromJson(text, Request::class.java)
