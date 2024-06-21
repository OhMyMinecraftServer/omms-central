package icu.takeneko.omms.central.controller.crashreport

import kotlinx.serialization.Serializable

@Serializable
data class CrashReportStorage(
    val controllerId: String,
    val timeMillis: Long,
    val content: List<String>
)

