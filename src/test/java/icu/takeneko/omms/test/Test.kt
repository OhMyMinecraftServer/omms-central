package icu.takeneko.omms.test

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.system.measureTimeMillis

class Test {
    @Test
    fun testParseKotlinX(){
        var fileContent: String
        val readFile = measureTimeMillis {
            val file = Path("./run/forest.json")
            fileContent = file.readText()
        }
        val time = measureTimeMillis {
            val jsonElement = Json.parseToJsonElement(fileContent)
        }
        println("readFile cost $readFile ms")
        println("kotlinx Time elapsed: $time ms")
    }

    @Test
    fun testParseGson(){
        var fileContent: String
        val readFile = measureTimeMillis {
            val file = Path("./run/forest.json")
            fileContent = file.readText()
        }
        val time = measureTimeMillis{
            val jElem = JsonParser.parseString(fileContent)
        }
        println("readFile cost $readFile ms")
        println("gson time elapsed: $time ms")
    }
}