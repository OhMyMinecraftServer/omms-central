package icu.takeneko.omms.central.system.file;

import java.nio.file.Path;

public record FileSystemDescriptor(String displayName, Path mountPoint) {
}
