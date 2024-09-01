package icu.takeneko.omms.central.network.chatbridge;

import icu.takeneko.omms.central.plugin.callback.ChatbridgeBroadcastReceivedCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class UdpBroadcastReceiver extends Thread {
    private static final Logger logger = LoggerFactory.getLogger("UdpBroadcastReceiver");

    public UdpBroadcastReceiver() {
        this.setName("UdpBroadcastReceiver#" + getId());
    }

    @Override
    public void run() {
        System.out.println();
        String oldId = "";
        int port = 10086;
        String address = "224.114.51.4"; // 224.114.51.4:10086
        try {
            MulticastSocket socket = new MulticastSocket(port);
            socket.setReuseAddress(true);
            InetAddress inetAddress = InetAddress.getByName(address);
            logger.info("Started Broadcast Receiver at " + address + ":" + port);
            socket.joinGroup(new InetSocketAddress(inetAddress, port), null);

            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            for (; ; ) {
                try {
                    socket.receive(packet);
                    String msg = new String(packet.getData(), packet.getOffset(),
                            packet.getLength(), StandardCharsets.UTF_8);
                    var broadcast = BroadcastKt.buildFromJson(msg);
                    if (broadcast != null && !oldId.equals(broadcast.getId())) {
                        ChatbridgeBroadcastReceivedCallback.INSTANCE.invokeAll(broadcast);
                        ChatMessageCache.INSTANCE.add(broadcast);
                        logger.info(String.format("%s <%s[%s]> %s", Objects.requireNonNull(broadcast).getChannel(), broadcast.getPlayer(), broadcast.getServer(), broadcast.getContent()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
