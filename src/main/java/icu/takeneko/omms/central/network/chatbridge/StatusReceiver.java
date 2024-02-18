package icu.takeneko.omms.central.network.chatbridge;

import icu.takeneko.omms.central.controller.Status;
import icu.takeneko.omms.central.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class StatusReceiver extends Thread {
    private final Logger logger = LoggerFactory.getLogger("UdpBroadcastReceiver");
    private final Target target;
    private MulticastSocket socket;
    private final HashMap<String, Status> statusHashMap = new HashMap<>();

    public StatusReceiver(Target target) {
        this.setName("StatusReceiver#" + getId());
        this.target = target;
    }

    public HashMap<String, Status> getStatusHashMap() {
        return statusHashMap;
    }

    public void end() {
        socket.close();
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            int port = target.getPort();
            String address = target.getAddress(); // 224.114.51.4:10086
            socket = new MulticastSocket(target.getPort());
            logger.info("Started Status Receiver at " + address + ":" + port);
            socket.joinGroup(new InetSocketAddress(InetAddress.getByName(address), port), NetworkInterface.getByInetAddress(InetAddress.getByName(address)));
            DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
            while (true) {
                try {
                    socket.receive(packet);
                    String msg = new String(packet.getData(), packet.getOffset(),
                            packet.getLength(), StandardCharsets.UTF_8);
                    var status = Util.fromJson(msg, Status.class);
                    status.setAlive(true);
                    status.setQueryable(true);
                    //System.out.println(msg);
                    System.out.println("Got status info from " + status.getName());
                    statusHashMap.put(status.getName(), status);
                } catch (SocketException ignored) {
                } catch (Exception e) {
                    socket.close();
                    e.printStackTrace();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
