package icu.takeneko.omms.central.network.http

import kotlinx.serialization.Serializable

enum class RequestStatus {
    ACCEPTED, REFUSED, PARTIAL
}

@Serializable
data class HttpResponseData(
    val status: RequestStatus = RequestStatus.ACCEPTED,
    val content: String = "",
    val refuseReason: String = "",
    val extra: Map<String, String> = mapOf()
)


@Serializable
data class WhitelistQueryData(val whitelistName: String, val players: List<String>)

@Serializable
data class WhitelistQueryResult(val acceptedPlayers: List<String>, val refusedPlayers: List<String>)

@Serializable
data class ControllerQueryData(val controllerId: String, val command: String = "")

@Serializable
data class AnnouncementQueryData(val announcementId: String)

@Serializable
data class BroadcastData(
    val channel: String = "GLOBAL",
    val playerName: String = "********",
    val server: String = "OMMS CENTRAL",
    val content: String
)
