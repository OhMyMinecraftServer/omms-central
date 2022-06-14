package net.zhuruoling.whitelist;

import com.google.gson.Gson;
import net.zhuruoling.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class WhitelistReader {
    List<Whitelist> whitelists = new ArrayList<>();
    Logger logger = LoggerFactory.getLogger("SControlClientFileReader");
    boolean fail = false;
    boolean noWhitelist = false;
    public WhitelistReader(){
        File folder = new File(new Util().getWorkingDir() + File.separator + "whitelists");
        if (folder.isDirectory() && folder.exists()){
            var v1 = folder.listFiles();
            assert v1 != null;
            if (v1.length == 0){
                noWhitelist = true;
            }
            for (File file:v1) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                } catch (FileNotFoundException e) {
                    logger.error("Failed to read whitelist Files.");
                    e.printStackTrace();
                    fail = true;
                    return;
                }
                Gson json = new Gson();
                Whitelist whitelist = json.fromJson(reader,Whitelist.class);
                whitelists.add(whitelist);
            }
        }
    }

    public boolean isNoWhitelist(){
        return noWhitelist;
    }

    public boolean isFail(){
        return fail;
    }

    public List<Whitelist> getWhitelists() {
        return whitelists;
    }

    public Whitelist read(String name){
        if (fail){
            return null;
        }
        AtomicReference<Whitelist> cl = new AtomicReference<>();
        whitelists.forEach(whitelist -> {
            if (Objects.equals(whitelist.name, name)){
                cl.set(whitelist);
            }

        });
        if (cl.get() == null){
            return null;
        }
        else {
            return cl.get();
        }
    }
}
