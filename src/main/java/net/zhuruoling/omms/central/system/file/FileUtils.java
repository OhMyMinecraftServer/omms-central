package net.zhuruoling.omms.central.system.file;

import net.zhuruoling.omms.central.system.OperatingSystem;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static @NotNull List<FileSystemDescriptor> getAllFileSystemDescriptors() {
        List<FileSystemDescriptor> fileSystemDescriptors = new ArrayList<>();
        switch (OperatingSystem.platformEnum) {
            case WINDOWS -> windowsListFileSystemDescriptorImpl(fileSystemDescriptors);
            case LINUX, ANDROID -> linuxListFileSystemDescriptorImpl(fileSystemDescriptors);
            case MACOS -> macOSListFileSystemDescriptorImpl(fileSystemDescriptors);
            default ->
                    throw new UnsupportedOperationException("Operating system not supported: " + OperatingSystem.platformEnum.getName());
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
