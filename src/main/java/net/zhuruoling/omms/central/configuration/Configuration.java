package net.zhuruoling.omms.central.configuration;

import com.google.gson.annotations.SerializedName;

public class Configuration {
    public Configuration(int port,String serverName, int httpPort){
        this.port = port;
        this.serverName = serverName;
        this.httpPort = httpPort;
    }
    public Configuration(int port,String serverName){
        this.port = port;
        this.serverName = serverName;
    }
    @SerializedName("port")
    int port;

    @SerializedName("http_port")
    int httpPort;

    @SerializedName("server_name")
    String serverName;

    @SerializedName("authorised_controller")
    String[] authorisedController = new String[]{};

    public String[] getAuthorisedController() {
        return authorisedController;
    }

    public void setAuthorisedController(String[] authorisedController) {
        this.authorisedController = authorisedController;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

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

    @Override
    public String toString() {
        return "Configuration{" +
                "port=" + port +
                ", httpPort=" + httpPort +
                ", serverName='" + serverName + '\'' +
                ", authorisedController=" + authorisedController +
                '}';
    }
}
