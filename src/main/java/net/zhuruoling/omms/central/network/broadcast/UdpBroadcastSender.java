package net.zhuruoling.omms.central.network.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class UdpBroadcastSender extends Thread {

    private final Logger logger = LoggerFactory.getLogger("UdpBroadcastSender");
    boolean stopped = false;
    private final ConcurrentHashMap<Target, byte[]> queue = new ConcurrentHashMap<>();
    private final HashMap<Target, MulticastSocket> multicastSocketCache = new HashMap<>();

    public UdpBroadcastSender() {
        this.setName("UdpBroadcastSender#" + this.getId());
    }

    public void createMulticastSocketCache(Target target) {
        try {
            multicastSocketCache.put(target, createMulticastSocket(target.getAddress(), target.getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearMulticastSocketCache(){
        multicastSocketCache.clear();
    }

    private static MulticastSocket createMulticastSocket(String addr, int port) throws IOException {
        MulticastSocket socket;
        InetAddress inetAddress;
        inetAddress = InetAddress.getByName(addr);
        socket = new MulticastSocket(port);
        socket.joinGroup(new InetSocketAddress(inetAddress, port), NetworkInterface.getByInetAddress(inetAddress));
        return socket;

    }

    @Override
    public void run() {
        logger.info("Starting UdpBroadcastSender.");
        while (!stopped) {
            try {
                if (!queue.isEmpty()) {
                    queue.forEach(this::send);
                }
                sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        logger.info("Stopped!");
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public void addToQueue(Target target, String content) {
        queue.put(target, content.getBytes(StandardCharsets.UTF_8));
    }

    private void send(Target target, byte[] content) {
        queue.remove(target,content);
        MulticastSocket socket;
        try {
            if (multicastSocketCache.containsKey(target)) {
                socket = multicastSocketCache.get(target);
            } else {
                socket = createMulticastSocket(target.getAddress(), target.getPort());
                multicastSocketCache.put(target,socket);
            }
            DatagramPacket packet = new DatagramPacket(content, content.length, new InetSocketAddress(target.getAddress(), target.getPort()).getAddress(), target.getPort());

            socket.send(packet);
        } catch (Exception e) {
            logger.error("Cannot send UDP Broadcast.\n\tTarget=%s\n\tContent=%s"
                            .formatted(target.toString(), Arrays.toString(content))
                    , e);
        }
    }



}