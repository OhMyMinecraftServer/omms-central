package net.zhuruoling.omms.central.controller;

import com.google.gson.annotations.Expose;
import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.ControllerConsoleImpl;
import net.zhuruoling.omms.central.controller.console.input.PrintTarget;
import net.zhuruoling.omms.central.controller.console.output.InputSource;
import net.zhuruoling.omms.central.controller.crashreport.CrashReportStorage;
import net.zhuruoling.omms.central.network.http.client.ControllerHttpClient;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ControllerImpl extends Controller {
    String name;

    String executable;

    String type;

    String launchParams;

    String workingDir;

    String httpQueryAddress;

    boolean statusQueryable;

    @Expose(serialize = false, deserialize = false)
    private ControllerHttpClient controllerHttpClient;

    public boolean isStatusQueryable() {
        return statusQueryable;
    }

    @Override
    public List<String> sendCommand(String command) {
        return null;
    }

    @Override
    public ControllerConsole startControllerConsole(InputSource inputSource, PrintTarget<?, ControllerConsole> printTarget, String id) {
        return ControllerConsoleImpl.newInstance(this, id, printTarget, inputSource);
    }


    @Override
    public Status queryControllerStatus() {
        return controllerHttpClient.queryStatus();
    }

    @Override
    public CrashReportStorage convertCrashReport(String raw) {
        return new CrashReportStorage(this.name, System.currentTimeMillis(), Arrays.stream(raw.split("\n")).toList());
    }

    public ControllerImpl() {
    }

    public void fixFields(){
        this.controllerHttpClient = new ControllerHttpClient(this);
    }

    public ControllerImpl(String name, String executable, String type, String launchCommand, String workingDir) {
        this.name = name;
        this.executable = executable;
        this.type = type;
        this.launchParams = launchCommand;
        this.workingDir = workingDir;
        this.controllerHttpClient = new ControllerHttpClient(this);
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
