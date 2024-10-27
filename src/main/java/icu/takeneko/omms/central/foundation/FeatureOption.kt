package icu.takeneko.omms.central.foundation

import org.slf4j.LoggerFactory

object FeatureOption {
    private val featureMap = mutableMapOf<String, Boolean>()
    private val logger = LoggerFactory.getLogger("Feature")

    fun parse(args: Array<String>) {
        for (arg in args) {
            if (arg.startsWith("--feature:")){
                featureMap[arg.removePrefix("--feature:")] = true
            }
        }
        if (featureMap.isNotEmpty()) {
            logger.info("Enabled features: " + featureMap.keys.joinToString(", "))
        }
    }

    operator fun get(f: String):Boolean{
        return featureMap.getOrDefault(f, false)
    }
}