package net.zhuruoling.omms.central.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import net.zhuruoling.omms.central.console.CommandSourceStack
import net.zhuruoling.omms.central.console.ConsoleCommandHandler
import net.zhuruoling.omms.central.main.RuntimeConstants.publicLogger
import net.zhuruoling.omms.central.util.Util

@OptIn(DelicateCoroutinesApi::class)
fun Route.commandUpstreamRouting(){
    route("/command"){
        post ("run"){
            if (this.context.request.header(HttpHeaders.UserAgent) != "omms controller"){
               this.context.respond(HttpStatusCode.Forbidden)
               return@post
            }
            val command = call.receiveText()
            println("Got upstream command: $command")
            GlobalScope.launch(Dispatchers.Default) {
                ensureActive()
                ConsoleCommandHandler.init()
                val sourceStack = CommandSourceStack(CommandSourceStack.Source.REMOTE)
                ConsoleCommandHandler().apply {
                    setLogger(publicLogger)
                    dispatchCommand(command, sourceStack)
                }
                this@post.context.respondText(contentType = ContentType.Text.Plain, status = HttpStatusCode.OK){
                    Util.toJson(object {
                        val feedback = sourceStack.feedbackLines
                    })
                }
            }

        }
    }
}