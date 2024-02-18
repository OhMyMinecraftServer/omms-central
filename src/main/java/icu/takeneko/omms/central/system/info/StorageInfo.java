package icu.takeneko.omms.central.system.info;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StorageInfo {
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

    public static String asJsonString(StorageInfo storageInfo) {
        return new GsonBuilder().serializeNulls().create().toJson(storageInfo);
    }
}
