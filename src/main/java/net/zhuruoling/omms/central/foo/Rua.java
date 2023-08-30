package net.zhuruoling.omms.central.foo;


import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import net.zhuruoling.omms.central.console.ConsoleInputHandler;
import org.jline.reader.LineReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");


    public static List<String> prependNumber(List<String> list) {
        AtomicInteger i = new AtomicInteger(1);
        return list.stream().map(s -> i.getAndAdd(1) + ": " + s).toList();
    }

    public static boolean endsWith(String a, String b){
        return a.endsWith(b);
    }

    public static void main(String[] args) throws Throwable {
        prependNumber(new ArrayList<>(List.of("a","b","c"))).forEach(System.out::println);
    }
}
