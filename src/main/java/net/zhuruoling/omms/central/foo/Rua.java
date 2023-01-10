package net.zhuruoling.omms.central.foo;


import net.zhuruoling.omms.central.util.UtilKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");

    public static void main(String[] args) throws InterruptedException {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        List<Integer> list = new ArrayList<>();
        for (int j = 0; j < 100000; j++) {
            var random = new Random(System.nanoTime());
            list.add(random.nextInt());
        }

        logger.info("begin sort");
        var arr = UtilKt.toTypedArray(list);
        Long begin = System.nanoTime();
        //list.sort(Integer::compareTo);
        Arrays.sort(arr);
        Long end = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            logger.info(String.valueOf(arr[i]));
        }
        logger.info(begin.toString());
        logger.info(end.toString());
        logger.info(String.valueOf((end - begin)));
    }
}
