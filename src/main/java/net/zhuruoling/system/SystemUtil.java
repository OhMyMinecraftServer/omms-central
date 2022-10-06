package net.zhuruoling.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashSet;
import java.util.Objects;

public class SystemUtil {
    Logger logger = LoggerFactory.getLogger("SystemUtil");
    private static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    public static String getSystemVersion(){
        return os.getVersion();
    }

    public static String getSystemName(){
        return os.getName();
    }

    public static String getSystemArch(){
        return os.getArch();
    }
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
            for (File f : Objects.requireNonNull(files)) {
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
        hardwareAbstractionLayer.getDiskStores().forEach(hwDiskStore -> storageInfo.getStorageList().add(storageInfo.new Storage(hwDiskStore.getName(), hwDiskStore.getModel(), hwDiskStore.getSize())));
        //没有静态(static)的类中类不能使用外部类进行.操作,必须用实例来进行实例化类中类.
        return storageInfo;
    }

    public static FileSystemInfo getFileSystemInfo(){
        SystemInfo systemInfo = new SystemInfo();
        FileSystemInfo fileSystemInfo = new FileSystemInfo();
        systemInfo.getOperatingSystem().getFileSystem().getFileStores().forEach(osFileStore -> fileSystemInfo.fileSystemList.add(new FileSystemInfo.FileSystem(osFileStore.getFreeSpace(), osFileStore.getTotalSpace(), osFileStore.getVolume(), osFileStore.getMount(), osFileStore.getType())));
        return fileSystemInfo;
    }

    public static NetworkInfo getNetworkInfo(){
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        var networkParams = systemInfo.getOperatingSystem().getNetworkParams();
        var networkInfo = new NetworkInfo(networkParams.getHostName(), networkParams.getDomainName(), networkParams.getDnsServers(), networkParams.getIpv4DefaultGateway(), networkParams.getIpv6DefaultGateway());
        hardwareAbstractionLayer.getNetworkIFs().forEach(networkIF -> networkInfo.getNetworkInterfaceList().add(new NetworkInfo.NetworkInterface(networkIF.getName(), networkIF.getDisplayName(), networkIF.getMacaddr(), networkIF.getMTU(), networkIF.getSpeed(), networkIF.getIPv4addr(), networkIF.getIPv6addr())));
        return networkInfo;
    }


}
