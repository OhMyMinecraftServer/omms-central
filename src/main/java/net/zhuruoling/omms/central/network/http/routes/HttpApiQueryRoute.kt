package net.zhuruoling.omms.central.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import net.zhuruoling.omms.central.command.CommandManager
import net.zhuruoling.omms.central.command.CommandSourceStack
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.network.chatbridge.Broadcast
import net.zhuruoling.omms.central.network.chatbridge.sendBroadcast
import net.zhuruoling.omms.central.network.http.*
import net.zhuruoling.omms.central.util.toStringMap

fun Route.httpApiQueryRouting() {
    route("/api") {
        route("/controller") {
            post("run") {
                val request = call.receive<ControllerQueryData>()
                try {
                    val result = ControllerManager.sendCommand(request.controllerId, request.command)
                    return@post call.respond(HttpResponseData(
                        content = result.result.joinToString("\n"),
                        extra = buildMap {
                            this["commandStatus"] = result.status.toString()
                            this["controllerExceptionMessage"] = result.exceptionMessage
                            this["controllerExceptionDetail"] = result.exceptionDetail
                        }
                    ))
                } catch (e: Exception) {
                    return@post call.respond(HttpResponseData(
                        status = RequestStatus.REFUSED,
                        content = "",
                        refuseReason = e.toString()
                    ))
                }
            }
            get("status") {
                val request = call.receive<ControllerQueryData>()
                try {
                    val result = ControllerManager.getControllerStatus(mutableListOf(request.controllerId))[request.controllerId]!!
                    return@get call.respond(HttpResponseData(
                        content = "",
                        extra = result.toStringMap()
                    ))
                } catch (e: Exception) {
                    return@get call.respond(HttpResponseData(
                        status = RequestStatus.REFUSED,
                        content = "",
                        refuseReason = e.toString()
                    ))
                }
            }
        }
        route("/whitelist") {
            post("add") {
                //whitelistName;username
                val request = call.receive<WhitelistQueryData>()

            }
            post("remove") {
                //whitelistName;username
                val request = call.receive<WhitelistQueryData>()
            }
            post("create") {
                //whitelistName
                val request = call.receive<WhitelistQueryData>()
            }
            delete("delete") {
                //whitelistName;username
                val request = call.receive<WhitelistQueryData>()
            }

        }
        route("/announcement") {
            get("list") {

            }
            get("content") {

            }
            post("create") {

            }
            delete("delete") {

            }
        }
        route("/broadcast") {
            post {
                val request = call.receive<BroadcastData>()
                val broadcast = Broadcast().apply {
                    channel = request.channel
                    content = request.content
                    player = request.playerName
                    server = request.server
                }
                launch {
                    sendBroadcast(broadcast)
                }
                return@post call.respond(HttpResponseData())
            }
        }
        post("command/run") {
            val command = call.receiveText()
            println("Got upstream command: $command")
            val sourceStack = CommandSourceStack(CommandSourceStack.Source.REMOTE)
            CommandManager.INSTANCE.dispatchCommand(
                command,
                CommandSourceStack(CommandSourceStack.Source.REMOTE)
            )
            this@post.context.respond(
                status = HttpStatusCode.OK,
                HttpResponseData(content = sourceStack.feedbackLines.joinToString("\n"))
            )
        }
    }
}
