package icu.takeneko.omms.central.plugin

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.plugin.depedency.PluginDependency
import icu.takeneko.omms.central.plugin.metadata.PluginDependencyRequirement
import java.lang.module.ModuleDescriptor
import java.util.regex.Pattern

val versionNamePattern: Pattern = Pattern.compile("([><=]=?)([0-9A-Za-z.]+)")

fun requirementMatches(self: PluginDependencyRequirement, dependency: PluginDependency): Boolean {
    if (self.id != dependency.id) return false
    if (self.symbol == "*")return true
    return when (self.symbol) {
        ">=" -> self.parsedVersion <= dependency.version
        "<=" -> self.parsedVersion >= dependency.version
        ">" -> self.parsedVersion < dependency.version
        "<" -> self.parsedVersion > dependency.version
        "==" -> self.parsedVersion == dependency.version
        else -> throw IllegalStateException("${self.symbol} is not a valid version comparator.")
    }
}

object PluginMetadataExclusionStrategy: ExclusionStrategy {
    override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
        return fieldAttributes.name == "symbol" || fieldAttributes.name == "parsedVersion" || fieldAttributes.declaredClass == ModuleDescriptor.Version::class.java
    }

    override fun shouldSkipClass(aClass: Class<*>?): Boolean {

        return false
    }
}

val gsonForPluginMetadata: Gson = GsonBuilder()
    .addDeserializationExclusionStrategy(PluginMetadataExclusionStrategy)
    .addSerializationExclusionStrategy(PluginMetadataExclusionStrategy)
    .create()