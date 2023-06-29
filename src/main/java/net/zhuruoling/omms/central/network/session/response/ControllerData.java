package net.zhuruoling.omms.central.network.session.response;

import net.zhuruoling.omms.central.controller.Controller;

public class ControllerData {
    //{\"name\":\"creative\",\"executable\":\"python\",\"type\":\"fabric\",\"launchParams\":\"-m mcdreforged\",\"workingDir\":\"..\\\\creative\",\"httpQueryAddress\":\"127.0.0.1:50014\",\"statusQueryable\":true}
    private ControllerData(){}
    private String name;
    private String type;
    private boolean statusQueryable;
    public static ControllerData fromController(Controller controller){
        var data = new ControllerData();
        data.name = controller.getName();
        data.statusQueryable = controller.isStatusQueryable();
        data.type = controller.getType();
        return data;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isStatusQueryable() {
        return statusQueryable;
    }
}
