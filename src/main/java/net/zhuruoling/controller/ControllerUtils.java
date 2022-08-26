package net.zhuruoling.controller;

public class ControllerUtils {
    public static void launchEZConfigure(){

    }

    public static ControllerTypes resloveTypeFromString(String type){
        return ControllerTypes.valueOf(type.toUpperCase());
    }
}
