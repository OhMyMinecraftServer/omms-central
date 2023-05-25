package net.zhuruoling.omms.central.foo;


import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import net.zhuruoling.omms.central.console.ConsoleInputHandler;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");

    public static void main(String[] args) throws Throwable {
        String[] cmd = {"powershell"};
        var env = System.getenv();
        PtyProcess process = new PtyProcessBuilder().setCommand(cmd).setUseWinConPty(true).setEnvironment(env).start();
        ConsoleInputHandler.INSTANCE.prepareTerminal();
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))){
                while (process.isAlive()){
                    System.out.println(reader.readLine());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        var lineReader = LineReaderBuilder.builder().terminal(ConsoleInputHandler.INSTANCE.getTerminal()).build();
        try (OutputStream os = process.getOutputStream()){
            while (process.isAlive()) {
               String line = lineReader.readLine();
               os.write(line.getBytes(StandardCharsets.UTF_8));
               os.write(process.getEnterKeyCode());
               os.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
