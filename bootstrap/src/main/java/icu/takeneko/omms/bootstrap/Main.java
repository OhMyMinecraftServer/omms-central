package icu.takeneko.omms.bootstrap;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Bootstrap: icu.takeneko.omms.bootstrap.Main.main");
        try {
            FileReader fr = new FileReader("meta.json");
            BootstrapMeta meta = new Gson().fromJson(fr, BootstrapMeta.class);
            fr.close();
            if (meta == null) {
                System.out.println("No Bootstrap meta provided!");
                System.exit(1);
            }
            URLClassLoader cl = new URLClassLoader(meta.getClasspath().stream().map(it -> {
                try {
                    return new File(it).toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).toList().toArray(new URL[]{}), Main.class.getClassLoader());
            Class<?> mainClazz = cl.loadClass(meta.getMainClass());
            Method method = mainClazz.getDeclaredMethod("main", String[].class);
            Thread.currentThread().setContextClassLoader(cl);
            method.invoke(null, (Object) args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
