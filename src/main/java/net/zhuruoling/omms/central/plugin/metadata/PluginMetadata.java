package net.zhuruoling.omms.central.plugin.metadata;

import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.util.Util;

public class PluginMetadata {
    String id;
    String version;
    String author;
    String link;
    @SerializedName(value = "main", alternate = {"pluginMain", "pluginMainClass"})
    String pluginMainClass;
    @SerializedName(value = "dependencies", alternate = {"pluginDependencies"})
    PluginDependencyRequirement[] pluginDependencies;
    @SerializedName(value = "pluginEventHandler", alternate = {"eventHandler", "handler"})
    String[] pluginEventHandlers;
    @SerializedName(value = "pluginRequestHandler", alternate = {"requestHandler"})
    String[] pluginRequestHandlers;

    public static PluginMetadata fromJson(String s) {
        return Util.fromJson(s, PluginMetadata.class);
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getLink() {
        return link;
    }

    public String getPluginMainClass() {
        return pluginMainClass;
    }

    public PluginDependencyRequirement[] getPluginDependencies() {
        return pluginDependencies;
    }

    public String[] getPluginEventHandlers() {
        return pluginEventHandlers;
    }

    public String[] getPluginRequestHandlers() {
        return pluginRequestHandlers;
    }
}
