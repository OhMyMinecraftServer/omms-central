package net.zhuruoling.omms.central.file;

import org.jetbrains.annotations.NotNull;
import oshi.PlatformEnum;
import oshi.SystemInfo;

import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static boolean IS_WINDOWS;
    public static boolean IS_LINUX;

    public static boolean IS_MACOS;

    private static final PlatformEnum platformEnum;

    static {
        platformEnum = SystemInfo.getCurrentPlatform();
        IS_WINDOWS = platformEnum == PlatformEnum.WINDOWS;
        IS_LINUX = platformEnum == PlatformEnum.LINUX || platformEnum == PlatformEnum.ANDROID;
        IS_MACOS = platformEnum == PlatformEnum.MACOS;
        if (!IS_WINDOWS && !IS_LINUX && !IS_MACOS) {
            throw new UnsupportedOperationException("Operating system not supported: " + platformEnum.getName());
        }
    }

    public static @NotNull List<FileSystemDescriptor> getAllFileSystemDescriptors() {
        List<FileSystemDescriptor> fileSystemDescriptors = new ArrayList<>();
        switch (platformEnum) {
            case WINDOWS -> windowsListFileSystemDescriptorImpl(fileSystemDescriptors);
            case LINUX, ANDROID -> linuxListFileSystemDescriptorImpl(fileSystemDescriptors);
            case MACOS -> macOSListFileSystemDescriptorImpl(fileSystemDescriptors);
            default ->
                    throw new UnsupportedOperationException("Operating system not supported: " + platformEnum.getName());
        }
        return fileSystemDescriptors;
    }

    private static void windowsListFileSystemDescriptorImpl(@NotNull List<FileSystemDescriptor> fileSystemDescriptors) {
        for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
            String s = fileStore.toString();
            String path = s.subSequence(s.indexOf('(') + 1, s.indexOf(')')).toString();
            fileSystemDescriptors.add(new FileSystemDescriptor(path, Path.of(path)));
        }
    }

    public static void linuxListFileSystemDescriptorImpl(@NotNull List<FileSystemDescriptor> fileSystemDescriptors) {
        for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
            String s = fileStore.toString();
            String path = s.subSequence(0, s.indexOf('(') - 1).toString();
            String[] parts = s.split(" ");
            Path of = Path.of(path);
            if (parts.length >= 2) {
                String name = parts[1];
                fileSystemDescriptors.add(new FileSystemDescriptor(name.substring(1, name.length() - 1), of));
            } else {
                fileSystemDescriptors.add(new FileSystemDescriptor(path, of));
            }
        }
    }

    private static void macOSListFileSystemDescriptorImpl(@NotNull List<FileSystemDescriptor> fileSystemDescriptors) {
        for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
            String s = fileStore.toString();
            System.out.println(s);
            String path = s.subSequence(0, s.indexOf('(') - 1).toString();
            Path mountPath = Path.of(path);
            fileSystemDescriptors.add(new FileSystemDescriptor(s.substring(s.indexOf("("), s.indexOf(")")), mountPath));
        }
    }
}
