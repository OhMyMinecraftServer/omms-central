package net.zhuruoling.plugin;

public class PluginMetadata {
    String id;
    String version;
    String author;
    public PluginMetadata(){
    }
    public PluginMetadata(String id, String version, String author){
        this.author = author;
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
