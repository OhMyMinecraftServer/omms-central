package net.zhuruoling.omms.central.foo;


import cn.hutool.core.util.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");

    public static void main(String[] args) throws Throwable {
        var clazzUnixMountEntry = ClassLoaderUtil.getClassLoader().loadClass("sun.nio.fs.UnixMountEntry");
        var clazzUnixFileStore = ClassLoaderUtil.getClassLoader().loadClass("sun.nio.fs.UnixFileStore");
        FileSystems.getDefault().getFileStores().forEach(fileStore -> {
            var store = clazzUnixFileStore.isInstance(fileStore);
            try {
                Field entryField = null;
                for (Field field : clazzUnixFileStore.getDeclaredFields()) {
                    if (field.getType() == clazzUnixMountEntry){
                        System.out.println(field.getName());
                        System.out.println(field.getType().getName());
                        entryField = field;
                        break;
                    }
                }
                if (entryField == null){
                    throw new NullPointerException("entryField");
                }
                entryField.setAccessible(true);
                var mountEntry = entryField.get(store);
                var dirField = clazzUnixMountEntry.getField("dir");
                dirField.setAccessible(true);
                var dir = (byte[]) dirField.get(mountEntry);
                System.out.println(new String(dir, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
