package net.zhuruoling.omms.central.network.session.server

import net.zhuruoling.omms.central.GlobalVariable.config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ServerSocket

class SessionLoginServer : Thread() {
    val logger: Logger = LoggerFactory.getLogger("SessionLoginServer")

    init {
        setName("SessionLoginServer#" + this.id)
    }

    override fun run() {
        try {
            ServerSocket(config!!.port).use { server ->
                logger.info("Started SessionLoginServer.")
                while (true) {
                    val socket = server.accept()
                    logger.debug(socket.getKeepAlive().toString())
                    socket.setKeepAlive(true)
                    val session =
                        LoginSession(socket)
                    session.start()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
