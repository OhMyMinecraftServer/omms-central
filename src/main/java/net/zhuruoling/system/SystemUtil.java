package net.zhuruoling.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SystemUtil {
    Logger logger = LoggerFactory.getLogger("SystemUtil");

    public static DirectoryInfo listDir(String path){
        DirectoryInfo info = new DirectoryInfo();
        File file = new File(path);
        if (file.isFile()){
            info.setResult(SystemResult.NOT_A_FOLDER);
        }
        else {
            var files = file.listFiles();
            HashSet<String> fileSet = new HashSet<>();
            HashSet<String> folderSet = new HashSet<>();
            for (File f : files) {
                if (f.isFile()){
                    fileSet.add(f.getName());
                    continue;
                }
                folderSet.add(f.getName());
            }
            info.setFiles(fileSet.stream().toList());
            info.setFolders(folderSet.stream().toList());
        }
        return info;
    }

    public static void print() {
        System.out.println("Initializing System...");
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();


        OperatingSystem os = si.getOperatingSystem();

        System.out.println(os);

        System.out.println("Checking Disks...");
        printDisks(hal.getDiskStores());

        System.out.println("Checking File System...");
        printFileSystem(os.getFileSystem());

        System.out.println("Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());

        System.out.println("Checking Network parameterss...");
        printNetworkParameters(os.getNetworkParams());

    }

    public static ProcessorInfo getProcessorInfo(){
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();
        var processorInfo = new ProcessorInfo();
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        double cpu = osMxBean.getSystemLoadAverage();
        processorInfo.setProcessorId(processor.getProcessorIdentifier().getProcessorID());
        processorInfo.setProcessorName(processor.getProcessorIdentifier().getName());
        processorInfo.setPhysicalCPUCount(processor.getPhysicalPackageCount());
        processorInfo.setLogicalProcessorCount(processor.getLogicalProcessorCount());
        processorInfo.setCpuLoadAvg(cpu);
        var sensors = hal.getSensors();
        processorInfo.setCpuTemp(sensors.getCpuTemperature());
        return processorInfo;
    }

    public static MemoryInfo getMemoryInfo(){
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        var memory = hardwareAbstractionLayer.getMemory();
        MemoryInfo memoryInfo = new MemoryInfo();
        memoryInfo.setMemoryTotal(memory.getTotal());
        memoryInfo.setMemoryUsed(memory.getAvailable());
        memoryInfo.setSwapTotal(memory.getVirtualMemory().getSwapTotal());
        memoryInfo.setSwapUsed(memory.getVirtualMemory().getSwapUsed());
        return memoryInfo;
    }

    public static StorageInfo getStorageInfo(){
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        StorageInfo storageInfo = new StorageInfo();
        return storageInfo;
    }


    private static void printDisks(List<HWDiskStore> list) {
        System.out.println("Disks:");
        for (HWDiskStore disk : list) {
            System.out.println(disk);
            System.out.println(disk.getName());
            System.out.println(disk.getModel());
            System.out.println(disk.getSize());
            System.out.println(FormatUtil.formatValue(disk.getSize(),"iB"));
            System.out.println();

        }
        System.exit(0);
    }

    private static void printFileSystem(FileSystem fileSystem) {
        System.out.println("File System:");

        System.out.format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors());

        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            System.out.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s%n",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());
        }
    }

    private static void printNetworkInterfaces(List<NetworkIF> list) {
        System.out.println("Network interfaces:");
        for (NetworkIF net : list) {
            System.out.format(" Name: %s (%s)%n", net.getName(), net.getDisplayName());
            System.out.format("   MAC Address: %s %n", net.getMacaddr());
            System.out.format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
            System.out.format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr()));
            System.out.format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr()));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            System.out.format("   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : "");
        }
    }

    private static void printNetworkParameters(NetworkParams networkParams) {
        System.out.println("Network parameters:");
        System.out.format(" Host name: %s%n", networkParams.getHostName());
        System.out.format(" Domain name: %s%n", networkParams.getDomainName());
        System.out.format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers()));
        System.out.format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway());
        System.out.format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway());
    }

}
