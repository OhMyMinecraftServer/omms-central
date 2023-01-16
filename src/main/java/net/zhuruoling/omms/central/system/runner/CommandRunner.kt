package net.zhuruoling.omms.central.system.runner

import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class RunnerDaemon constructor(
    val runnerId: String,
    val launchCommand: String,
    val workingDir: String,
    val description: String
):
    Thread("Runner-Daemon-$runnerId"){
    private val logger = LoggerFactory.getLogger("RunnerDaemon")
    private lateinit var out: OutputStream
    private lateinit var input: InputStream
    var running = false
    private val queue = ArrayBlockingQueue<String>(1024)
    private var process: Process? = null
    private var processOutputReader: ProcessOutputReader? = null
    var started = false
    var startFailReason: RuntimeException? = null

    private fun resolveCommand(command: String): Array<out String> {
        if (command.isEmpty()) throw IllegalArgumentException("Illegal command $command, to short or empty!")
        val stringTokenizer = StringTokenizer(command)
        val list = mutableListOf<String>()
        while (stringTokenizer.hasMoreTokens()) {
            list.add(stringTokenizer.nextToken())
        }
        return list.toTypedArray()
    }

    val reader get() = processOutputReader

    val processAlive: Boolean
        get() {
            return if (running && started)
                process!!.isAlive
            else
                throw IllegalStateException("Runner has not started.")
        }

    val returnCode: Int
        get() {
            return if (!running or !processAlive) {
                throw IllegalStateException("Runner is still running.")
            } else {
                process!!.exitValue()
            }
        }


    override fun run() {
        started = true
        logger.info("Runner started.")
        try {
            process = Runtime.getRuntime().exec(resolveCommand(launchCommand), null, File(workingDir))
            out = process!!.outputStream
            input = process!!.inputStream
        } catch (e: Exception) {
            startFailReason = RuntimeException("StartupFail",e)
            logger.error("Cannot start runner.", e)
            return
        }
        processOutputReader = ProcessOutputReader(process!!, runnerId)
        processOutputReader!!.start()
        val writer = out.writer(Charset.defaultCharset())
        while (process!!.isAlive) {
            running = true
            try {
                if (queue.isNotEmpty()) {
                    synchronized(queue) {
                        while (queue.isNotEmpty()) {
                            val line = queue.poll()
                            logger.info("input $line")
                            writer.write(line + "\n")
                            writer.flush()
                        }
                    }
                }
                sleep(10)
            } catch (e: ConcurrentModificationException) {
                e.printStackTrace()
            }
        }
        running = false
        logger.info("Process exited with exit value ${process!!.exitValue()}")
    }

    fun input(str: String) {
        synchronized(queue) {
            queue.add(str)
        }
    }

    fun terminate() {
        if (running) {
            if (processAlive) {
                process!!.destroyForcibly()
            } else {
                throw IllegalStateException("Process already exited.")
            }
        } else {
            throw IllegalStateException("Runner has not started.")
        }
    }
}


class ProcessOutputReader(private val process: Process, runnerId: String) :
    Thread("Runner-Output-$runnerId") {
    private val logger = LoggerFactory.getLogger("Runner-Output-$runnerId")
    private lateinit var input: InputStream
    private var lineIndex = 0;
    private val outputLines = mutableListOf<String>()
    override fun run() {
        logger.info("Process output reader started.")
        try {
            input = process.inputStream
            val reader = input.bufferedReader(Charset.forName("utf-8"))
            while (process.isAlive) {
                sleep(10)
                while (input.available() > 0){
                    val li = reader.readLine()
                    if (li != null) {
                        synchronized(outputLines) {
                            outputLines.add(li)
                        }
                    }
                }
            }
        } catch (ignored: InterruptedException) { }
    }

    fun nextLine(): String {
        synchronized(outputLines) {
            return if (hasNextLine()) {
                throw IndexOutOfBoundsException("Has no next line.")
            } else {
                lineIndex++
                outputLines[lineIndex + 1]
            }
        }
    }

    fun hasNextLine() = outputLines.size - 1 == lineIndex

    fun getAllLines() = outputLines

    fun getAllLinesAfterLineIndex(): MutableList<String> {
        return if (hasNextLine()) {
            val t = outputLines.subList(lineIndex, outputLines.size - 1)
            lineIndex = outputLines.size - 1
            t
        } else {
            mutableListOf()
        }
    }

}