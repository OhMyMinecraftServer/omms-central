package icu.takeneko.omms.central.network.session.handler

import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result

operator fun Request.get(key: String): String? {
    return this.getContent(key)
}

operator fun Request.contains(key: String):Boolean{
    return this.containsKey(key)
}

operator fun Response.set(key: String, value: String){
    withContentPair(key, value)
}

operator fun Response.plus(result: Result): Response{
    return this.withResponseCode(result)
}