package net.zhuruoling.system;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FileSystemInfo { // TODO: 2022/9/10

    @SerializedName("filesystems")
    final
    List<FileSystem> fileSystemList = new ArrayList<>();
    record FileSystem(long free, long total, String volume, String mountPoint, String fileSystemType){

    }

    public static String asJsonString(FileSystemInfo fileSystemInfo){
        return new GsonBuilder().serializeNulls().create().toJson(fileSystemInfo);
    }
}
