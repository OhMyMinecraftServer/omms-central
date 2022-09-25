package net.zhuruoling.system;

public record SystemInfo(String osName, String osVersion, String osArch, FileSystemInfo fileSystemInfo, MemoryInfo memoryInfo, NetworkInfo networkInfo,
                         ProcessorInfo processorInfo, StorageInfo storageInfo) {
}
