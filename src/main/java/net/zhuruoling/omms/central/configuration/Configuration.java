package net.zhuruoling.omms.central.configuration;

import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.network.ChatbridgeImplementation;

import java.util.Arrays;

public class Configuration {
    public Configuration(int port,String serverName, int httpPort){
        this.port = port;
        this.serverName = serverName;
        this.httpPort = httpPort;
        rateLimit = 1000;
    }

    @SerializedName("port")
    int port;

    @SerializedName("http_port")
    int httpPort;

    @SerializedName("rate_limit")
    int rateLimit;

    @SerializedName("server_name")
    String serverName;

    @SerializedName("authorised_controller")
    String[] authorisedController = new String[]{};
    @SerializedName("chatbridge_impl")
    ChatbridgeImplementation chatbridgeImplementation = ChatbridgeImplementation.UDP;

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

    public int getRateLimit() {
        return rateLimit;
    }

    public ChatbridgeImplementation getChatbridgeImplementation() {
        if (chatbridgeImplementation == null)return null;
        return chatbridgeImplementation;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "port=" + port +
                ", httpPort=" + httpPort +
                ", rateLimit=" + rateLimit +
                ", serverName='" + serverName + '\'' +
                ", authorisedController=" + Arrays.toString(authorisedController) +
                ", chatbridgeImplementation=" + chatbridgeImplementation +
                '}';
    }
}
