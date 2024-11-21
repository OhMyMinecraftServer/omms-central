package icu.takeneko.omms.central.network.http

fun <T> joinToString(list: List<T>): String {
    return list.joinToString(separator = "\n")
}

fun <T> joinToString(list: List<T>, separator: String): String {
    return list.joinToString(separator)
}
