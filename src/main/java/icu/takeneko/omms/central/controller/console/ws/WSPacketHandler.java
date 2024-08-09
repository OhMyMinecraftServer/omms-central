package icu.takeneko.omms.central.controller.console.ws;

import java.util.List;

public interface WSPacketHandler {
    void onConnect(int version);

    void onDisconnect();

    void onLog(List<String> line);
}
