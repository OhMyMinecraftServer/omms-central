package icu.takeneko.omms.central.controller;

import com.google.gson.annotations.Expose;
import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.controller.console.ControllerConsoleImpl;
import icu.takeneko.omms.central.controller.console.input.InputSource;
import icu.takeneko.omms.central.controller.console.output.PrintTarget;
import icu.takeneko.omms.central.controller.crashreport.CrashReportStorage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@AllArgsConstructor
public class ControllerImpl extends Controller {
    @Getter
    protected String name;
    protected String displayName;
    @Getter
    protected String type;
    @Getter
    protected String httpQueryAddress;
    @Getter
    protected boolean statusQueryable;
    private ControllerHttpClient controllerHttpClient;

    @Override
    public CommandExecutionResult sendCommand(@NotNull String command) throws Exception {
        var future = controllerHttpClient.sendCommand(command);
        return future.get();
    }

    @Override
    public @NotNull ControllerConsole startControllerConsole(InputSource.InputSourceFactory factory, PrintTarget<?, ControllerConsole> printTarget, String id) {
        return ControllerConsoleImpl.newInstance(this, id, printTarget, factory);
    }


    @Override
    public @NotNull Status queryControllerStatus() {
        return controllerHttpClient.queryStatus();
    }

    @Override
    public @NotNull CrashReportStorage convertCrashReport(@NotNull String raw) {
        return new CrashReportStorage(this.name, System.currentTimeMillis(), Arrays.stream(raw.split("\n")).toList());
    }

    public ControllerImpl(String name, String displayName, String type, String httpQueryAddress, boolean statusQueryable) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.httpQueryAddress = httpQueryAddress;
        this.statusQueryable = statusQueryable;
        controllerHttpClient = new ControllerHttpClient(this);
    }

    @Override
    public String getDisplayName() {
        return displayName == null ? name : displayName;
    }
}
