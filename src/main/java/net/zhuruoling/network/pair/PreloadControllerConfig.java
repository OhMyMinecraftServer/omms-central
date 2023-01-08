package net.zhuruoling.network.pair;

import java.util.ArrayList;
import java.util.List;

public class PreloadControllerConfig {
    String httpQueryAddress = "";
    String chatChannel = "";
    String allowedFakePlayerPrefix = "";
    String allowedFakePlayerSuffix = "";
    boolean enableWhitelist = true;
    boolean enableJoinMotd = true;
    boolean enableRemoteControl = true;
    boolean enableChatBridge = true;
    List<ServerMapping> serverMappings = new ArrayList<>();

    public PreloadControllerConfig() {
    }

    public PreloadControllerConfig(String httpQueryAddress,
                                   String chatChannel,
                                   String allowedFakePlayerPrefix,
                                   String allowedFakePlayerSuffix,
                                   boolean enableWhitelist,
                                   boolean enableJoinMotd,
                                   boolean enableRemoteControl,
                                   boolean enableChatBridge,
                                   List<ServerMapping> serverMappings) {
        this.httpQueryAddress = httpQueryAddress;
        this.chatChannel = chatChannel;
        this.allowedFakePlayerPrefix = allowedFakePlayerPrefix;
        this.allowedFakePlayerSuffix = allowedFakePlayerSuffix;
        this.enableWhitelist = enableWhitelist;
        this.enableJoinMotd = enableJoinMotd;
        this.enableRemoteControl = enableRemoteControl;
        this.enableChatBridge = enableChatBridge;
        this.serverMappings = serverMappings;
    }

    public String getHttpQueryAddress() {
        return httpQueryAddress;
    }

    public void setHttpQueryAddress(String httpQueryAddress) {
        this.httpQueryAddress = httpQueryAddress;
    }

    public String getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(String chatChannel) {
        this.chatChannel = chatChannel;
    }

    public String getAllowedFakePlayerPrefix() {
        return allowedFakePlayerPrefix;
    }

    public void setAllowedFakePlayerPrefix(String allowedFakePlayerPrefix) {
        this.allowedFakePlayerPrefix = allowedFakePlayerPrefix;
    }

    public String getAllowedFakePlayerSuffix() {
        return allowedFakePlayerSuffix;
    }

    public void setAllowedFakePlayerSuffix(String allowedFakePlayerSuffix) {
        this.allowedFakePlayerSuffix = allowedFakePlayerSuffix;
    }

    public List<ServerMapping> getServerMappings() {
        return serverMappings;
    }

    public void setServerMappings(List<ServerMapping> serverMappings) {
        this.serverMappings = serverMappings;
    }

    public boolean isEnableWhitelist() {
        return enableWhitelist;
    }

    public void setEnableWhitelist(boolean enableWhitelist) {
        this.enableWhitelist = enableWhitelist;
    }

    public boolean isEnableJoinMotd() {
        return enableJoinMotd;
    }

    public void setEnableJoinMotd(boolean enableJoinMotd) {
        this.enableJoinMotd = enableJoinMotd;
    }

    public boolean isEnableRemoteControl() {
        return enableRemoteControl;
    }

    public void setEnableRemoteControl(boolean enableRemoteControl) {
        this.enableRemoteControl = enableRemoteControl;
    }

    public boolean isEnableChatBridge() {
        return enableChatBridge;
    }

    public void setEnableChatBridge(boolean enableChatBridge) {
        this.enableChatBridge = enableChatBridge;
    }
}
