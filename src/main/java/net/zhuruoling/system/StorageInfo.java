package net.zhuruoling.system;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StorageInfo { // TODO: 2022/9/10
    @SerializedName("storages")
    List<Storage> storageList = new ArrayList<>();
    public class Storage{
        final String name;
        final String model;
        final long size;
        public Storage(String name, String model, long size) {
            this.name = name;
            this.model = model;
            this.size = size;
        }
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
