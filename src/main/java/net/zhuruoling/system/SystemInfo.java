package net.zhuruoling.system;

public record SystemInfo(FileSystemInfo fileSystemInfo, MemoryInfo memoryInfo, NetworkInfo networkInfo,
                         ProcessorInfo processorInfo, StorageInfo storageInfo) {
}
