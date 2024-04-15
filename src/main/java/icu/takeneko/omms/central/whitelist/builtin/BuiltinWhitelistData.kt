package icu.takeneko.omms.central.whitelist.builtin

import kotlinx.serialization.Serializable

@Serializable
data class BuiltinWhitelistData(
    val name: String,
    val aliases: List<String> = listOf(),
    val players: List<String> = listOf()
)
