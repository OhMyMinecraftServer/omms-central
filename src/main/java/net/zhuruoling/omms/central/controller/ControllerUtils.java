package net.zhuruoling.omms.central.controller;

public class ControllerUtils {
    public static void launchEZConfigure(){

    }

    public static ControllerTypes resloveTypeFromString(String type){
        return ControllerTypes.valueOf(type.toUpperCase());

    }
}
