package net.zhuruoling.omms.central.network.broadcast;

public class Target {

    private final String address;
    private final int port;

    public Target(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return (address + port).hashCode();
    }

    @Override
    public String toString() {
        return "Target{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }
}