package net.zhuruoling.omms.central.api.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigProviderBuilder {

    List<ExclusionStrategy> exclusionStrategyList = new ArrayList<>();

    List<TypeAdapter<?>> typeAdapterList = new ArrayList<>();
    boolean serializeNulls = false;

    public ConfigProviderBuilder() {
    }

    public ConfigProviderBuilder addExclusionSrategy(ExclusionStrategy exclusionStrategy) {
        ensureNotNull("exclusionStrategy", exclusionStrategy);
        this.exclusionStrategyList.add(exclusionStrategy);
        return this;
    }

    public ConfigProviderBuilder addTypeAdapter(TypeAdapter<? extends  Object> typeAdapter){
        ensureNotNull("typeAdapter", typeAdapter);
        this.typeAdapterList.add(typeAdapter);
        return this;
    }

    public ConfigProviderBuilder serializeNulls(){
        serializeNulls = true;
        return this;
    }

    private void ensureNotNull(String description, Object value) {
        if (value == null) {
            throw new NullPointerException("%s is null.".formatted(description));
        }
    }

    public ConfigProvider build(){
        return new ConfigProvider(exclusionStrategyList, typeAdapterList, serializeNulls);
    }
}
