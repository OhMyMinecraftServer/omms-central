package net.zhuruoling.omms.central.script;

import org.jetbrains.annotations.NotNull;

import java.lang.module.ModuleDescriptor;
import java.util.Collections;
import java.util.List;

public class ScriptMetadata {
    String id;
    ModuleDescriptor.Version version;
    List<String> author;


    public ScriptMetadata(String id, @NotNull String version, List<String> author) {
        this.author = author;
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
    }

    public ScriptMetadata(String id, @NotNull String version, String author) {
        this.author = Collections.singletonList(author);
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
    }

    public ScriptMetadata(String id, @NotNull String version) {
        this.id = id;
        this.version = ModuleDescriptor.Version.parse(version);
    }

    public ScriptMetadata(String id, ModuleDescriptor.Version version, List<String> author) {
        this.author = author;
        this.id = id;
        this.version = version;
    }

    public ScriptMetadata(String id, ModuleDescriptor.Version version, String author) {
        this.author = Collections.singletonList(author);
        this.id = id;
        this.version = version;
    }


}
