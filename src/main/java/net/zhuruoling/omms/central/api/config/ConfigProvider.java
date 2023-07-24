package net.zhuruoling.omms.central.api.config;

import kotlin.NotImplementedError;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ConfigProvider {

    List<ExclusionStrategy> exclusionStrategyList = new ArrayList<>();
    List<TypeAdapter<?>> typeAdapterList = new ArrayList<>();
    boolean serializeNulls = false;

    public ConfigProvider(){

    }

    public ConfigProvider(List<ExclusionStrategy> exclusionStrategyList, List<TypeAdapter<?>> typeAdapterList, boolean serializeNulls) {
        this.exclusionStrategyList = exclusionStrategyList;
        this.typeAdapterList = typeAdapterList;
        this.serializeNulls = serializeNulls;
    }

    public <T> String serialize(T object, Class<? extends T> clazz){
        throw new NotImplementedError("TODO");
    }
    public <T> String serialize(@NotNull T object){
        return serialize(object, object.getClass());
    }

    


    public <T> T deserialize(String source, Class<? extends T> clazz){
        throw new NotImplementedError("TODO");
    }
}
