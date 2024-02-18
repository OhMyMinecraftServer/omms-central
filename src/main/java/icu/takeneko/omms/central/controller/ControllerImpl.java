package icu.takeneko.omms.central.controller;

import com.google.gson.annotations.Expose;
import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.controller.console.ControllerConsoleImpl;
import icu.takeneko.omms.central.controller.console.input.InputSource;
import icu.takeneko.omms.central.controller.console.output.PrintTarget;
import icu.takeneko.omms.central.controller.crashreport.CrashReportStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ControllerImpl extends Controller {
    String name;
    String displayName;
    String type;
    String httpQueryAddress;
    boolean statusQueryable;
    String mcdrCommandPrefix = "!!";

    @Expose(serialize = false, deserialize = false)
    private ControllerHttpClient controllerHttpClient;

    public boolean isStatusQueryable() {
        return statusQueryable;
    }

    @Override
    public CommandExecutionResult sendCommand(@NotNull String command) throws Exception {
        var future = controllerHttpClient.sendCommand(command);
        return future.get();
    }

    @Override
    public @NotNull ControllerConsole startControllerConsole(InputSource inputSource, PrintTarget<?, ControllerConsole> printTarget, String id) {
        return ControllerConsoleImpl.newInstance(this, id, printTarget, inputSource);
    }


    @Override
    public @NotNull Status queryControllerStatus() {
        return controllerHttpClient.queryStatus();
    }

    @Override
    public @NotNull CrashReportStorage convertCrashReport(@NotNull String raw) {
        return new CrashReportStorage(this.name, System.currentTimeMillis(), Arrays.stream(raw.split("\n")).toList());
    }

    public ControllerImpl() {
    }

    public void fixFields() {
        this.controllerHttpClient = new ControllerHttpClient(this);
        if (this.mcdrCommandPrefix == null) {
            this.mcdrCommandPrefix = "!!";
        }
    }

    public ControllerImpl(String name, String type) {
        this.name = name;
        this.type = type;
        this.controllerHttpClient = new ControllerHttpClient(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ControllerImpl{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type='" + type + '\'' +
                ", httpQueryAddress='" + httpQueryAddress + '\'' +
                ", statusQueryable=" + statusQueryable +
                ", mcdrCommandPrefix='" + mcdrCommandPrefix + '\'' +
                ", controllerHttpClient=" + controllerHttpClient +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getHttpQueryAddress() {
        return httpQueryAddress;
    }

    @Override
    public String getDisplayName() {
        return displayName == null ? name : displayName;
    }
}
