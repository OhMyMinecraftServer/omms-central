package net.zhuruoling.network.broadcast;

public class Target {

    final String address;
    final int port;

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
}
