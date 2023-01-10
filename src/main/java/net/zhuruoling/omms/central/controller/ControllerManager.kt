package net.zhuruoling.omms.central.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.main.RuntimeConstants
import net.zhuruoling.omms.central.network.broadcast.StatusReceiver
import net.zhuruoling.omms.central.network.broadcast.Target
import net.zhuruoling.omms.central.util.Util
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.FutureTask

object ControllerManager {
    val controllers = mutableMapOf<String, ControllerInstance>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().serializeNulls().create()

    fun init() {
        controllers.clear()
        val path = File(Util.joinFilePaths("controllers"))
        val files =
            path.list(FilenameFilter { _, name -> return@FilenameFilter name.split(".")[name.split(".").size - 1] == "json" })
        if (files != null) {
            if (files.isEmpty()) {
                logger.warn("No Controller added to this server.")
                return
            } else {
                files.forEach {
                    logger.debug("server:$it")
                    val controller: Controller =
                        gson.fromJson(FileReader(Util.joinFilePaths("./controllers/", it)), Controller().javaClass)
                    logger.debug(controller.toString())
                    try {
                        controllers[controller.name] =
                            ControllerInstance(controller, ControllerUtils.resloveTypeFromString(controller.type))
                    } catch (e: IllegalArgumentException) {
                        logger.error(
                            "Cannot resolve controller type symbol: %s".format(controller.type),
                            IllegalControllerTypeException(
                                "Cannot resolve controller type symbol: %s".format(controller.type),
                                e
                            )
                        )
                    }
                }
            }
        } else {
            logger.warn("No Controller added to this server.")
            return
        }
    }

    fun sendInstruction(controllerName: String, command: String) {
        getControllerByName(controllerName)?.let { this.sendInstruction(it, command) }
    }

    fun sendInstruction(instance: ControllerInstance, command: String) {
        val instruction = Instruction(instance.controllerType, instance.controller.name, command)
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(instruction))
    }

    private fun sendInstruction(command: Instruction) {
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(command))
    }


    //controller execute survival give @a dirt
    @Nullable
    fun getControllerByName(name: String): ControllerInstance? {
        if (controllers.containsKey(name)) {
            return controllers[name]
        }
        return null
    }

    @NotNull
    fun getControllerStatuses(): MutableMap<String, Status> {
        val map = mutableMapOf<String, Status>()
        println("Fetching controller statuses.")
        val target = Util.generateRandomTarget()
        val receiver = StatusReceiver(target)
        receiver.start()
        this.controllers.forEach {
            if (it.value.controller.isStatusQueryable) {
                this.sendInstruction(
                    Instruction(
                        it.value.controllerType,
                        it.key,
                        Util.toJson(target),
                        InstructionType.UPLOAD_STATUS
                    )
                )
            }
        }
        Thread.sleep(1500)
        receiver.end()
        val list = receiver.statusHashMap
        this.controllers.forEach {
            map[it.key] = if (list.containsKey(it.key)) list.getValue(it.key) else Status()
        }
        return map
    }

    fun newStatusReciever(target: Target): FutureTask<MutableMap<String, Status>> {
        return FutureTask {
            val map = mutableMapOf<String, Status>()
            try {
                val port: Int = target.port
                val address: String = target.address // 224.114.51.4:10086
                val socket = MulticastSocket(target.port)
                logger.info("Started Status Receiver at $address:$port")
                socket.joinGroup(
                    InetSocketAddress(InetAddress.getByName(address), port),
                    NetworkInterface.getByInetAddress(InetAddress.getByName(address))
                )
                val packet = DatagramPacket(ByteArray(8192), 8192)
                while (true) {
                    try {
                        socket.receive(packet)
                        val msg = java.lang.String(
                            packet.data, packet.offset,
                            packet.length, StandardCharsets.UTF_8
                        )
                        val status = Util.fromJson(
                            msg.toString(),
                            Status::class.java
                        )
                        status.setAlive(true)
                        status.setQueryable(true)
                        println("Got status info from " + status.getName())
                        map[status.getName()] = status
                    } catch (ignored: SocketException) {
                        println("Error occurred while receiving data.$ignored")
                    } catch (e: Exception) {
                        socket.close()
                        e.printStackTrace()
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@FutureTask map
        }

    }


}
