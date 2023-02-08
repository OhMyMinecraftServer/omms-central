package net.zhuruoling.omms.central.configuration;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Configuration {
    public Configuration(int port,String serverName, int httpPort){
        this.port = port;
        this.serverName = serverName;
        this.httpPort = httpPort;
        packetLimit = 1000;
    }

    @SerializedName("port")
    int port;

    @SerializedName("http_port")
    int httpPort;

    @SerializedName("packet_limit")
    int packetLimit;

    @SerializedName("server_name")
    String serverName;

    @SerializedName("authorised_controller")
    String[] authorisedController = new String[]{};

    public String[] getAuthorisedController() {
        return authorisedController;
    }


    public int getHttpPort() {
        return httpPort;
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

    public int getPacketLimit() {
        return packetLimit;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "port=" + port +
                ", httpPort=" + httpPort +
                ", packetLimit=" + packetLimit +
                ", serverName='" + serverName + '\'' +
                ", authorisedController=" + Arrays.toString(authorisedController) +
                '}';
    }
}
