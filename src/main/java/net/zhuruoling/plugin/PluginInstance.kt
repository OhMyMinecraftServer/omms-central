package net.zhuruoling.plugin

import javax.script.Invocable

class PluginInstance(invocable: Invocable, pluginStatus: PluginStatus, metadata: PluginMetadata) {

    var invocable : Invocable? = null
    var pluginStatus : PluginStatus = PluginStatus.NONE
    var pluginMetadata: PluginMetadata = PluginMetadata()
    init {
        this.invocable = invocable
        this.pluginMetadata = metadata
        this.pluginStatus = pluginStatus
    }
}