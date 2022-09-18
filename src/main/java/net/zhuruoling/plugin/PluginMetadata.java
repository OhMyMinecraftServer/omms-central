package net.zhuruoling.plugin;

import java.lang.module.ModuleDescriptor;
import java.util.Collections;
import java.util.List;

public class PluginMetadata {
    String id;
    ModuleDescriptor.Version version;
    List<String> author;

    PluginDependency pluginDependencies;
    public PluginMetadata(){

    }
    public PluginMetadata(String id, String version, List<String> author){
        this.author = author;
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
        pluginDependencies= null;
    }

    public PluginMetadata(String id, String version, String author){
        this.author = Collections.singletonList(author);
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
        pluginDependencies= null;
    }

    public PluginMetadata(String id, String version, List<String> author, PluginDependency pluginDependencies) {
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
        this.author = author;
        this.pluginDependencies = pluginDependencies;
    }

    public PluginMetadata(String id, String version, String author, PluginDependency pluginDependencies) {
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
        this.author = Collections.singletonList(author);
        this.pluginDependencies = pluginDependencies;
    }


    public PluginMetadata(String id, String version) {
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
    }

    public PluginMetadata(String id, ModuleDescriptor.Version version, List<String> author){
        this.author = author;
        this.id = id;
        this.version = version;
        pluginDependencies= null;
    }

    public PluginMetadata(String id, ModuleDescriptor.Version version, String author){
        this.author = Collections.singletonList(author);
        this.id = id;
        this.version = version;
        pluginDependencies= null;
    }

    public PluginMetadata(String id, ModuleDescriptor.Version version, List<String> author, PluginDependency pluginDependencies) {
        this.id = id;
        this.version = version;
        this.author = author;
        this.pluginDependencies = pluginDependencies;
    }

    public PluginMetadata(String id, ModuleDescriptor.Version version, String author, PluginDependency pluginDependencies) {
        this.id = id;
        this.version = version;
        this.author = Collections.singletonList(author);
        this.pluginDependencies = pluginDependencies;
    }


    public PluginMetadata(String id, ModuleDescriptor.Version version) {
        this.id = id;
        this.version = version;
    }



    @Override
    public String toString() {
        return "PluginMetadata{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", author=" + author +
                ", pluginDependencies=" + pluginDependencies +
                '}';
    }
}
