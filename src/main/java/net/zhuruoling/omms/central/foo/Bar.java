package net.zhuruoling.omms.central.foo;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Bar {
    public static void main(String[] args) {
        try (OutputStream os = new FileOutputStream("example.txt")){
            String str = "你干嘛";
            for (byte b : str.getBytes(StandardCharsets.UTF_8)) {
                os.write(b);
            }
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
