package net.zhuruoling.whitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class WhitelistManager {
    
    public WhitelistManager(){

    }
    public void addToWhitelist(String whitelistName, String value){

    }

    public void createWhitelist(String whitelistName){

    }

    public void checkAndFix(){

    }
    public static WhitelistResult queryWhitelist(String whitelistName, String value){
        boolean returnValue = false;
        WhitelistReader whitelistReader = new WhitelistReader();
        if (whitelistReader.isNoWhitelist()) {
            return WhitelistResult.NO_WHITELIST;
        }
        var whitelist = whitelistReader.read(whitelistName);
        if (whitelist == null) {
           return WhitelistResult.WHITELIST_NOT_EXIST;
        }
        else {
            if (whitelist.containsPlayer(value))
                return WhitelistResult.OK;
            return WhitelistResult.NO_SUCH_PLAYER;
        }
    }

    public static WhitelistResult addToWhiteList(String whitelistName, String value){
        Whitelist whitelist = new WhitelistReader().read(whitelistName);
        var before = whitelist.getPlayers();
        String[] after = new String[before.length + 1];
        var beforeList1 = new ArrayList<String>(Arrays.stream(before).toList());
        if (beforeList1.contains(value)) {
            return WhitelistResult.PLAYER_ALREADY_EXISTS;
        }
        beforeList1.add(value);
        after = beforeList1.toArray(after);
        Whitelist newWhitelist = new Whitelist(after, whitelistName);
        Gson gson1 = new GsonBuilder().serializeNulls().create();
        String cont = gson1.toJson(newWhitelist);
        File fp = new File(Util.getWorkingDir() + File.separator + "whitelists" + File.separator + whitelistName + ".json");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(fp);
            OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
            writer.append(cont);
            writer.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return WhitelistResult.FAIL;
        }

        return WhitelistResult.OK;
    }

    public static WhitelistResult removeFromWhiteList(String whitelistName, String value){

        Whitelist whitelist1 = new WhitelistReader().read(whitelistName);
        String[] before1 = whitelist1.getPlayers();
        String[] after1 = new String[before1.length - 1];
        var beforeList = new ArrayList<String>(Arrays.stream(before1).toList());
        if (!beforeList.contains(value)) {
            return WhitelistResult.NO_SUCH_PLAYER;
        }
        beforeList.remove(value);
        after1 = beforeList.toArray(after1);
        Whitelist whitelist2 = new Whitelist(after1, whitelistName);
        Gson gson2 = new GsonBuilder().serializeNulls().create();
        String s = gson2.toJson(whitelist2);
        File file = new File(Util.getWorkingDir() + File.separator + "whitelists" + File.separator + whitelistName + ".json");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.append(s);
            outputStreamWriter.close();
            fileOutputStream.close();
            return WhitelistResult.OK;
        }
        catch (Exception e){
            e.printStackTrace();
            return WhitelistResult.FAIL;
        }
    }


}
