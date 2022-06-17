package net.zhuruoling.configuration;

import com.google.gson.annotations.SerializedName;
import net.zhuruoling.util.Util;
public class Configuration {
    public Configuration(int port,String serverName,String key,String cryptoKey){
        this.port = port;
        this.serverName = serverName;
    }
    public Configuration(int port,String serverName){
        this.port = port;
        this.serverName = serverName;
    }
    @SerializedName("port")
    public int port;
    @SerializedName("server_name")
    public String serverName;


    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

}
