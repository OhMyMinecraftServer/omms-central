package net.zhuruoling.omms.central.plugin.metadata;

import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.plugin.UtilKt;

import java.util.List;

public class PluginMetadata {
    String id;
    String version;
    String author;
    String link;
    @SerializedName(value = "main", alternate = {"pluginMain", "pluginMainClass"})
    String pluginMainClass;
    @SerializedName(value = "dependencies", alternate = {"pluginDependencies"})
    List<PluginDependencyRequirement> pluginDependencies;
    @SerializedName(value = "pluginRequestHandler", alternate = {"requestHandler"})
    List<String> pluginRequestHandlers;

    public static PluginMetadata fromJson(String s) {
        return UtilKt.getGsonForPluginMetadata().fromJson(s, PluginMetadata.class);
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

    public List<PluginDependencyRequirement> getPluginDependencies() {
        return pluginDependencies;
    }

    public List<String> getPluginRequestHandlers() {
        return pluginRequestHandlers;
    }
}
