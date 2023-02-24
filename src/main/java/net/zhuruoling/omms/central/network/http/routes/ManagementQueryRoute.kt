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
fun Route.managementQueryRouting() {
    route("/management") {
        route("/controller") {
            post("run") {
                
            }
            get(""){

            }
        }
        route("/whitelist") {
            post("add") {
                //whitelistName;username
                val json = call.receiveText()

            }
            post("remove") {
                //whitelistName;username

            }
            post("create") {
                //whitelistName

            }
            post("delete") {
                //whitelistName;username

            }
        }
        route("/announcement") {
            post("create") {

            }
            post("delete") {

            }
        }
        route("/broadcast"){
            post("send") {

            }
        }
        route("/permission"){
            route("{id?}") {
                get {

                }
                get("resolve") {

                }

            }
            post("{operation?}") {

            }
            post("calculate") {

            }
        }
        post ("command/run"){
            if (this.context.request.header(HttpHeaders.UserAgent) != "omms controller"){
                this.context.respond(HttpStatusCode.Forbidden)
                return@post
            }
            val command = call.receiveText()
            println("Got upstream command: $command")
            GlobalScope.launch(Dispatchers.Default) {
                ensureActive()
                val sourceStack =
                    CommandSourceStack(CommandSourceStack.Source.REMOTE)
                CommandManager.INSTANCE.dispatchCommand(command,
                    CommandSourceStack(CommandSourceStack.Source.REMOTE)
                )
                this@post.context.respondText(contentType = ContentType.Text.Plain, status = HttpStatusCode.OK){
                    Util.toJson(object {
                        val feedback = sourceStack.feedbackLines
                    })
                }
            }

        }
    }
}
