package net.zhuruoling.scontrol;

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
import java.util.concurrent.atomic.AtomicReference;

public class SControlClientFileReader {
    List<SControlClient> clientList = new ArrayList<>();
    Logger logger = LoggerFactory.getLogger("SControlClientFileReader");
    boolean fail = false;
    boolean noClient = false;
    public SControlClientFileReader(){
        File folder = new File(new Util().getWorkingDir() + File.separator + "clients");
        if (folder.isDirectory() && folder.exists()){
            var v1 = folder.listFiles();
            assert v1 != null;
            if (v1.length == 0){
                noClient = true;
            }
            for (File file:v1) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                } catch (FileNotFoundException e) {
                    logger.error("Failed to read sControl Client Files.");
                    e.printStackTrace();
                    fail = true;
                    return;
                }
                Gson json = new Gson();
                SControlClient config = json.fromJson(reader,SControlClient.class);
                clientList.add(config);
            }
        }
    }

    public boolean isNoClient(){
        return noClient;
    }

    public boolean isFail(){
        return fail;
    }

    public List<SControlClient> getClientList() {
        return clientList;
    }

    public SControlClient read(String name){
        if (fail){
            return null;
        }
        AtomicReference<SControlClient> cl = new AtomicReference<>();
        clientList.forEach(client -> {
            if (client.name == name){
                cl.set(client);
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
