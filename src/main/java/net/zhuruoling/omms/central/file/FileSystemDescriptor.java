package net.zhuruoling.omms.central.file;

import java.nio.file.Path;

public record FileSystemDescriptor(String displayName, Path mountPath) {
}
