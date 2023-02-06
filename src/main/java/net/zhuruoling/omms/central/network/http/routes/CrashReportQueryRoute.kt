package net.zhuruoling.omms.central.network.http.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.crashReportQueryRoute() {
    val logger = LoggerFactory.getLogger("CrashReportUpload")
    route("/controller"){
        post("crashReport/upload") {
            val content = call.receiveText()

            for (line in content.split("\n"))
                logger.info(line)
        }
    }
}
