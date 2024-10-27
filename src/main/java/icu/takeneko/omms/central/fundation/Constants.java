package icu.takeneko.omms.central.fundation;

import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastTarget;
import icu.takeneko.omms.central.network.session.request.LoginRequest;

public class Constants {
    public static final long VERSION_BASE = 0xc0000000L;
    public static final long PROTOCOL_VERSION = VERSION_BASE + 0x12;
    public static final String PRODUCT_NAME = "Oh My Minecraft Server Central Server";
    public static final String PRODUCT_NAME_SHORT = "OMMS Central Server";
    public static final UdpBroadcastTarget TARGET_CHAT = new UdpBroadcastTarget("224.114.51.4", 10086);
    public static final String[] DATA_FOLDERS = {
            "controllers",
            "announcements",
            "whitelists",
            "plugins",
            "scripts"
    };
}
