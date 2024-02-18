package icu.takeneko.omms.central.system.info;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class NetworkInfo {
    private List<NetworkInterface> networkInterfaceList = new ArrayList<>();
    private String hostName;
    private String domainName;
    private String[] dnsServers;
    private String ipv4DefaultGateway;
    private String ipv6DefaultGateway;

    public NetworkInfo(String hostName, String domainName, String[] dnsServers, String ipv4DefaultGateway, String ipv6DefaultGateway) {
        this.hostName = hostName;
        this.domainName = domainName;
        this.dnsServers = dnsServers;
        this.ipv4DefaultGateway = ipv4DefaultGateway;
        this.ipv6DefaultGateway = ipv6DefaultGateway;
    }

    public List<NetworkInterface> getNetworkInterfaceList() {
        return networkInterfaceList;
    }

    public void setNetworkInterfaceList(List<NetworkInterface> networkInterfaceList) {
        this.networkInterfaceList = networkInterfaceList;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String[] getDnsServers() {
        return dnsServers;
    }

    public void setDnsServers(String[] dnsServers) {
        this.dnsServers = dnsServers;
    }

    public String getIpv4DefaultGateway() {
        return ipv4DefaultGateway;
    }

    public void setIpv4DefaultGateway(String ipv4DefaultGateway) {
        this.ipv4DefaultGateway = ipv4DefaultGateway;
    }

    public String getIpv6DefaultGateway() {
        return ipv6DefaultGateway;
    }

    public void setIpv6DefaultGateway(String ipv6DefaultGateway) {
        this.ipv6DefaultGateway = ipv6DefaultGateway;
    }

    public record NetworkInterface(String name, String displayName, String macAddress, long mtu, long speed,
                                   String[] ipv4Address, String[] ipv6Address) {
    }

    public static String toJsonString(NetworkInfo networkInfo) {
        return new GsonBuilder().serializeNulls().create().toJson(networkInfo);
    }
}
