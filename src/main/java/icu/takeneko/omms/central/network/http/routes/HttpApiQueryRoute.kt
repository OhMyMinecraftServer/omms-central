package icu.takeneko.omms.central.network.http.routes

import icu.takeneko.omms.central.command.CommandManager
import icu.takeneko.omms.central.command.CommandSourceStack
import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.controller.crashreport.ControllerCrashReportManager
import icu.takeneko.omms.central.network.chatbridge.Broadcast
import icu.takeneko.omms.central.network.chatbridge.send
import icu.takeneko.omms.central.network.http.*
import icu.takeneko.omms.central.network.session.FailureReasons
import icu.takeneko.omms.central.system.info.SystemInfoUtil
import icu.takeneko.omms.central.util.toStringMap
import icu.takeneko.omms.central.whitelist.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.httpApiQueryRouting() {
    route("/api") {
        route("controller") {
            post("run") {
                val request = call.receive<ControllerQueryData>()
                try {
                    val result = ControllerManager.sendCommand(request.controllerId, request.command)
                    return@post call.respond(
                        HttpResponseData(
                            content = result.result.joinToString("\n"),
                            extra = buildMap {
                                this["commandStatus"] = result.status.toString()
                                this["controllerExceptionMessage"] = result.exceptionMessage
                                this["controllerExceptionDetail"] = result.exceptionDetail
                            }
                        ))
                } catch (e: Exception) {
                    return@post call.respond(
                        HttpResponseData(
                            status = RequestStatus.REFUSED,
                            content = "",
                            refuseReason = e.toString()
                        )
                    )
                }
            }
            get("status") {
                val request = call.receive<ControllerQueryData>()
                try {
                    val result =
                        ControllerManager.getControllerStatus(mutableListOf(request.controllerId))[request.controllerId]!!
                    return@get call.respond(
                        HttpResponseData(
                            content = "",
                            extra = result.toStringMap()
                        )
                    )
                } catch (e: Exception) {
                    return@get call.respond(
                        HttpResponseData(
                            status = RequestStatus.REFUSED,
                            content = "",
                            refuseReason = e.toString()
                        )
                    )
                }
            }
            route("crashReport") {
                get("latest") {
                    val request = call.receive<ControllerQueryData>()
                    val result =
                        ControllerCrashReportManager.getLatest(request.controllerId) ?: return@get call.respond(
                            HttpResponseData(
                                content = ""
                            )
                        )
                    return@get call.respond(
                        HttpResponseData(
                            content = result.content.joinToString(separator = "\n")
                        )
                    )
                }
            }
        }
        route("whitelist") {
            post("add") {
                //whitelistName;username
                val request = call.receive<WhitelistQueryData>()
                if (request.whitelistName !in WhitelistManager.getWhitelistNames()) {
                    return@post call.respond(
                        status = HttpStatusCode.BadRequest,
                        HttpResponseData(RequestStatus.REFUSED, refuseReason = FailureReasons.WHITELIST_NOT_FOUND)
                    )
                }
                return@post try {
                    WhitelistManager.addToWhiteList(request.whitelistName, request.playerName)
                    WhitelistManager.flush(request.whitelistName)
                    call.respond(status = HttpStatusCode.OK, HttpResponseData())
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        HttpResponseData(RequestStatus.REFUSED, refuseReason = FailureReasons.PLAYER_EXISTS)
                    )
                }
            }
            post("remove") {
                val request = call.receive<WhitelistQueryData>()
                if (request.whitelistName !in WhitelistManager.getWhitelistNames()) {
                    return@post call.respond(
                        status = HttpStatusCode.BadRequest,
                        HttpResponseData(RequestStatus.REFUSED, refuseReason = FailureReasons.WHITELIST_NOT_FOUND)
                    )
                }
                return@post try {
                    WhitelistManager.removeFromWhiteList(request.whitelistName, request.playerName)
                    WhitelistManager.flush(request.whitelistName)
                    call.respond(status = HttpStatusCode.OK, HttpResponseData())
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        HttpResponseData(RequestStatus.REFUSED, refuseReason = FailureReasons.PLAYER_NOT_FOUND)
                    )
                }
            }
            post("create") {
                //whitelistName
                val request = call.receive<WhitelistQueryData>()
                return@post try {
                    WhitelistManager.createWhitelist(request.whitelistName)
                    call.respond(HttpResponseData())
                } catch (_: WhitelistAlreadyExistsException) {
                    call.respond(
                        HttpResponseData(
                            RequestStatus.REFUSED,
                            refuseReason = FailureReasons.WHITELIST_EXISTS
                        )
                    )
                }
            }
            delete("delete") {
                //whitelistName;username
                val request = call.receive<WhitelistQueryData>()
                return@delete try {
                    WhitelistManager.deleteWhiteList(request.whitelistName)
                    call.respond(HttpResponseData())
                } catch (_: WhitelistNotExistException) {
                    call.respond(
                        HttpResponseData(
                            RequestStatus.REFUSED,
                            refuseReason = FailureReasons.WHITELIST_EXISTS
                        )
                    )
                }
            }
            get("list") {
                return@get call.respond(status = HttpStatusCode.OK,WhitelistManager.getWhitelistNames().toList())
            }
        }
        route("/broadcast") {
            post {
                val request = call.receive<BroadcastData>()
                val broadcast = Broadcast(
                    request.channel,
                    request.server,
                    request.playerName,
                    request.content
                )
                launch {
                    broadcast.send()
                }
                return@post call.respond(HttpResponseData())
            }
        }
        post("command/run") {
            val command = call.receiveText()
            println("Got upstream command: $command")
            val sourceStack =
                CommandSourceStack(CommandSourceStack.Source.REMOTE)
            CommandManager.INSTANCE.dispatchCommand(
                command,
                CommandSourceStack(CommandSourceStack.Source.REMOTE)
            )
            this@post.context.respond(
                status = HttpStatusCode.OK,
                HttpResponseData(content = sourceStack.feedbackLines.joinToString("\n"))
            )
        }
        route("system") {
            get("brief") {
                val si = SystemInfoUtil.getSystemInfo()
                val status = SystemStatusInfo(
                    "${si.osName} ${si.osVersion} ${si.osArch}",
                    listOf(si.processorInfo.cpuLoadAvg),
                    si.processorInfo.cpuFreqs,
                    si.processorInfo.logicalProcessorCount,
                    si.processorInfo.cpuTemp.toFloat(),
                    si.memoryInfo.memoryTotal,
                    si.memoryInfo.memoryUsed,
                    System.currentTimeMillis()
                )
                call.respond(status)
            }
        }
    }
}
