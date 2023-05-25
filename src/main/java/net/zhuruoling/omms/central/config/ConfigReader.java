package net.zhuruoling.omms.central.config;

import com.google.gson.Gson;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger("ConfigReader");

    @Nullable
    public static Configuration read(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Util.joinFilePaths("config.json")));
            Gson json = new Gson();
            Configuration config = json.fromJson(reader,Configuration.class);
            if (!(config == null)){
                return config;
            }
            else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
