package icu.takeneko.omms.central.network.session.response;

import icu.takeneko.omms.central.controller.Controller;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ControllerData {
    //{\"name\":\"creative\",\"executable\":\"python\",\"type\":\"fabric\",\"launchParams\":\"-m mcdreforged\",\"workingDir\":\"..\\\\creative\",\"httpQueryAddress\":\"127.0.0.1:50014\",\"statusQueryable\":true}
    private ControllerData() {
    }

    private String name;
    private String type;
    private String displayName;
    private boolean statusQueryable;

    public static @NotNull ControllerData fromController(@NotNull Controller controller) {
        var data = new ControllerData();
        data.name = controller.getName();
        data.statusQueryable = controller.isStatusQueryable();
        data.displayName = controller.getDisplayName();
        data.type = controller.getType();
        return data;
    }

}
