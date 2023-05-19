package net.zhuruoling.omms.central.foo;


import net.zhuruoling.omms.central.file.FileSystemDescriptor;
import net.zhuruoling.omms.central.file.FileUtils;
import net.zhuruoling.omms.central.util.UtilKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");



    public static void main(String[] args) throws Throwable {
        UtilKt.printRuntimeEnv();
        for (FileSystemDescriptor fileSystemDescriptor : FileUtils.getAllFileSystemDescriptors()) {
            logger.info(fileSystemDescriptor.toString());
        }
    }
}
