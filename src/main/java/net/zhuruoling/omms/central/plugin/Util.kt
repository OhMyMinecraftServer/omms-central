package net.zhuruoling.omms.central.plugin

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency
import net.zhuruoling.omms.central.plugin.metadata.PluginDependencyRequirement
import java.util.regex.Pattern

val versionNamePattern: Pattern = Pattern.compile("([><=]=?)([0-9A-Za-z.]+)")

fun requirementMatches(self: PluginDependencyRequirement, dependency: PluginDependency): Boolean {
    if (self.id != dependency.id) return false
    return when (self.symbol) {
        ">=" -> self.version >= dependency.version
        "<=" -> self.version <= dependency.version
        ">" -> self.version >= dependency.version
        "<" -> self.version <= dependency.version
        "==" -> self.version >= dependency.version
        else -> throw IllegalStateException("${self.symbol} is not a valid version comparator.")
    }
}

object PluginMetadataExclusionStrategy: ExclusionStrategy {
    override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
        return fieldAttributes.name == "symbol" || fieldAttributes.name == "version"
    }

    override fun shouldSkipClass(aClass: Class<*>?): Boolean {
        return false
    }
}

val gsonForPluginMetadata = GsonBuilder()
    .addDeserializationExclusionStrategy(PluginMetadataExclusionStrategy)
    .create()