package net.zhuruoling.omms.central.system;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StorageInfo { // TODO: 2022/9/10
    @SerializedName("storages")
    List<Storage> storageList = new ArrayList<>();

    public record Storage(String name, String model, long size) {
    }

    public List<Storage> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<Storage> storageList) {
        this.storageList = storageList;
    }

    public static String asJsonString(StorageInfo storageInfo){
        return new GsonBuilder().serializeNulls().create().toJson(storageInfo);
    }
}
