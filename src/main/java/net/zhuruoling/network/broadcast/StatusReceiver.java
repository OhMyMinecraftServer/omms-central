package net.zhuruoling.network.broadcast;

import net.zhuruoling.controller.Status;
import net.zhuruoling.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class StatusReceiver extends Thread{
    private final Logger logger = LoggerFactory.getLogger("UdpBroadcastReceiver");
    private final Target target;
    private final HashMap<String, Status> statusHashMap = new HashMap<>();
    public StatusReceiver(Target target){
        this.setName("StatusReceiver#" + getId());
        this.target = target;
    }

    public HashMap<String, Status> getStatusHashMap() {
        return statusHashMap;
    }

    @Override
    public void run() {
        try {
            int port = target.port;
            String address = target.address; // 224.114.51.4:10086
            MulticastSocket socket;
            InetAddress inetAddress = InetAddress.getByName(address);
            socket = new MulticastSocket(target.port);
            logger.info("Started Status Receiver at " + address + ":" + port);
            socket.joinGroup(new InetSocketAddress(InetAddress.getByName(address),port), NetworkInterface.getByInetAddress(InetAddress.getByName(address)));

            DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
            while (true){
                try {
                    socket.receive(packet);
                    String msg = new String(packet.getData(), packet.getOffset(),
                            packet.getLength(), StandardCharsets.UTF_8);
                    var status = Util.fromJson(msg, Status.class);
                    status.setAlive(true);
                    System.out.println(msg);
                    statusHashMap.put(status.getName(), status);
                }
                catch (Exception ignored){
                    if (!(ignored instanceof InterruptedException)){
                        ignored.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
