package icu.takeneko.omms.central.util.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import java.util.*

data class LogEvent(
    val time: Date = Date(),
    val logLevel: Level,
    val thread: String,
    val loggerName: String,
    val logMessage: String
) {
    companion object {
        fun create(loggingEvent: ILoggingEvent) = LogEvent(
            logLevel = loggingEvent.level,
            thread = loggingEvent.threadName,
            loggerName = loggingEvent.loggerName,
            logMessage = loggingEvent.formattedMessage.replace("\t", "    ")
        )
    }
}
