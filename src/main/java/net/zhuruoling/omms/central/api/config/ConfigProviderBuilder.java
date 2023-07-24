package net.zhuruoling.omms.central.api.config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigProviderBuilder {

    @NotNull List<ExclusionStrategy> exclusionStrategyList = new ArrayList<>();

    @NotNull List<TypeAdapter<?>> typeAdapterList = new ArrayList<>();
    boolean serializeNulls = false;

    public ConfigProviderBuilder() {
    }

    public @NotNull ConfigProviderBuilder addExclusionSrategy(@NotNull ExclusionStrategy exclusionStrategy) {
        ensureNotNull("exclusionStrategy", exclusionStrategy);
        this.exclusionStrategyList.add(exclusionStrategy);
        return this;
    }

    public @NotNull ConfigProviderBuilder addTypeAdapter(@NotNull TypeAdapter<? extends  Object> typeAdapter){
        ensureNotNull("typeAdapter", typeAdapter);
        this.typeAdapterList.add(typeAdapter);
        return this;
    }

    public @NotNull ConfigProviderBuilder serializeNulls(){
        serializeNulls = true;
        return this;
    }

    private void ensureNotNull(String description, @NotNull Object value) {
        if (value == null) {
            throw new NullPointerException("%s is null.".formatted(description));
        }
    }

    public @NotNull ConfigProvider build(){
        return new ConfigProvider(exclusionStrategyList, typeAdapterList, serializeNulls);
    }
}
