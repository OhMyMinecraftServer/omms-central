package net.zhuruoling.omms.central.controller;

import org.jetbrains.annotations.NotNull;

public class ControllerUtils {
    public static void launchEZConfigure(){

    }

    public static @NotNull ControllerTypes resloveTypeFromString(@NotNull String type){
        return ControllerTypes.valueOf(type.toUpperCase());

    }
}
