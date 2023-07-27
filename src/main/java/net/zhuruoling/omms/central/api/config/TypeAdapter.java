package net.zhuruoling.omms.central.api.config;

public interface TypeAdapter <T>{
    T parse(String raw);
}
