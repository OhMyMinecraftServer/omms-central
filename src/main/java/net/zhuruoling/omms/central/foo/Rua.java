package net.zhuruoling.omms.central.foo;


import cn.hutool.core.exceptions.ExceptionUtil;
import net.zhuruoling.omms.central.util.UtilKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");

    public static void main(String[] args) throws Throwable {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        logger.info("WDNMD");

    }
}
