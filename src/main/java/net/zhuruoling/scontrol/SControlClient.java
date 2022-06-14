package net.zhuruoling.scontrol;

import com.google.gson.annotations.SerializedName;

public class SControlClient {
    public SControlClient(String type, String name, int port, String ip, String key, String path, String workingDir){
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.key = key;
        this.path = path;
    }
    @SerializedName("type")
    String type = "";
    @SerializedName("ip")
    String ip = "";
    @SerializedName("port")
    int port = 0;
    @SerializedName("name")
    String name;
    @SerializedName("key")
    String key;
    @SerializedName("path")
    String path;
    @SerializedName("working_dir")
    String workingDir;

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SControlClient{" +
                "type='" + type + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", path='" + path + '\'' +
                ", workingDir='" + workingDir + '\'' +
                '}';
    }
}
