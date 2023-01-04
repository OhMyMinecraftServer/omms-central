package net.zhuruoling.network.http.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import net.zhuruoling.whitelist.WhitelistManager

@OptIn(DelicateCoroutinesApi::class)
fun Route.managementQueryRouting() {
    route("/management") {
        route("/controller") {
            post("run") {
                
            }
        }
        route("/whitelist") {
            post("add") {
                val json = call.receiveText()

            }
            post("remove") {

            }
            post("create") {

            }
            post("delete") {

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
    }
}
