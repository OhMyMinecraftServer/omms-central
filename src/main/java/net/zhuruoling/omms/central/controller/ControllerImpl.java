package net.zhuruoling.omms.central.controller;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.input.PrintTarget;
import net.zhuruoling.omms.central.controller.console.output.InputSource;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ControllerImpl extends Controller {
    private String name;

    private String executable;

    private String type;

    private String launchParams;

    private String workingDir;

    private String httpQueryAddress;

    private boolean statusQueryable;

    public boolean isStatusQueryable() {
        return statusQueryable;
    }

    @Override
    public List<String> sendCommand(String command) {
        return null;
    }

    @Override
    public ControllerConsole startControllerConsole(InputSource inputSource, PrintTarget<?, ?> printTarget, String id) {
        return null;
    }


    @Override
    public Status getControllerStatus() {
        return null;
    }

    public ControllerImpl() {
    }

    public ControllerImpl(String name, String executable, String type, String launchCommand, String workingDir) {
        this.name = name;
        this.executable = executable;
        this.type = type;
        this.launchParams = launchCommand;
        this.workingDir = workingDir;
    }

    public String getName() {
        return name;
    }

    @Override
    public @NotNull String toString() {
        return "Controller{" +
                "name='" + name + '\'' +
                ", executable='" + executable + '\'' +
                ", type='" + type + '\'' +
                ", launchCommand='" + launchParams + '\'' +
                ", workingDir='" + workingDir + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutable() {
        return executable;
    }

    public String getType() {
        return type;
    }

    public String getLaunchParams() {
        return launchParams;
    }

    public String getWorkingDir() {
        return workingDir;
    }


    public String toJson() {
        return Util.gson.toJson(this);
    }


    public String getHttpQueryAddress() {
        return httpQueryAddress;
    }

}
