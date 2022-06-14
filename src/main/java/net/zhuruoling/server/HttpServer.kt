package net.zhuruoling.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.zhuruoling.configuration.ConfigReader
import net.zhuruoling.server.plugins.configureRouting
import org.slf4j.LoggerFactory

class HttpServer : Thread("HttpServerKt") {
    private val logger = LoggerFactory.getLogger("HTTPServer")
    override fun run(){
        var httpPort : Int = (ConfigReader.read()?.getPort() ?: Int) as Int
        httpPort++
        logger.info(
            "Started Uranium qLink HTTP service at $httpPort"
        )
        embeddedServer(Netty, port = httpPort, host = "0.0.0.0") {
            configureRouting()
        }.start(wait = true)
    }
}