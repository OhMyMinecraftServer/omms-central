package net.zhuruoling.omms.central.api.config;

@FunctionalInterface
public interface ExclusionStrategy {
    boolean exclude(Class<?> clazz);
}
