package net.zhuruoling.plugin;

import java.util.Collections;
import java.util.List;

public class PluginMetadata {
    String id;
    String version;
    List<String> author;

    List<PluginDependency> pluginDependencies;
    public PluginMetadata(){

    }
    public PluginMetadata(String id, String version, List<String> author){
        this.author = author;
        this.id = id;
        this.version = version;
        pluginDependencies= null;
    }

    public PluginMetadata(String id, String version, String author){
        this.author = Collections.singletonList(author);
        this.id = id;
        this.version = version;
        pluginDependencies= null;
    }

    public PluginMetadata(String id, String version, List<String> author, List<PluginDependency> pluginDependencies) {
        this.id = id;
        this.version = version;
        this.author = author;
        this.pluginDependencies = pluginDependencies;
    }


    public PluginMetadata(String id, String version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
