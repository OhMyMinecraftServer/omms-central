package icu.takeneko.omms.central.network.session.server

import icu.takeneko.omms.central.config.Config.config
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SessionLoginServer : Thread() {
    val logger: Logger = LoggerFactory.getLogger("SessionLoginServer")

    init {
        setName("SessionLoginServer#" + this.id)
    }

    override fun run() {
        try {
            runBlocking {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val serverSocket = aSocket(selectorManager).tcp().bind("0.0.0.0", config.port)

                while (true) {
                    val socket = serverSocket.accept()
                    GlobalScope.launch {
                        try {
                            val receiveChannel = socket.openReadChannel()
                            val sendChannel = socket.openWriteChannel(autoFlush = true)
                            val session = LoginSession(socket, receiveChannel, sendChannel)
                            val sessionServer = session.processLogin() ?: return@launch
                            sessionServer.handleRequest()
                        } catch (e: Throwable) {
                            logger.error("Unable to handle incoming connection.", e)
                            socket.close()
                        }
                    }
                }
            }
        } catch (_: InterruptedException) {

        }
    }
}
