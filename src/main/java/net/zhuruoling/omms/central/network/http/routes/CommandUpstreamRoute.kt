package net.zhuruoling.omms.central.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import net.zhuruoling.omms.central.command.CommandManager
import net.zhuruoling.omms.central.command.CommandSourceStack
import net.zhuruoling.omms.central.util.Util

@OptIn(DelicateCoroutinesApi::class)
fun Route.commandUpstreamRouting() {
    route("/command") {
        post {
            launch {
                val text = call.receiveText()
                val source = CommandSourceStack(CommandSourceStack.Source.REMOTE)
                return@launch try {
                    CommandManager.INSTANCE.dispatchCommand(text, source)
                    call.respondText {
                        Util.toJson(object {
                            val result = true
                            val content = source.feedbackLines
                        })
                    }
                } catch (e: Exception) {
                    call.respondText {
                        Util.toJson(object {
                            val result = true
                            val content = mutableListOf<String>()
                            val exceptionMessage = e.toString()
                            val exceptionDetail = e.stackTraceToString()
                        })
                    }
                }
            }
        }
    }
}