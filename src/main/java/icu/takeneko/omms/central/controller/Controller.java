package icu.takeneko.omms.central.controller;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.controller.console.input.InputSource;
import icu.takeneko.omms.central.controller.console.output.PrintTarget;
import icu.takeneko.omms.central.controller.crashreport.CrashReportStorage;
import icu.takeneko.omms.central.foundation.IdHolder;

abstract public class Controller implements IdHolder {
    public abstract boolean isStatusQueryable();

    public abstract CommandExecutionResult sendCommand(String command) throws Exception;

    public abstract ControllerConsole startControllerConsole(
            InputSource.InputSourceFactory inputSource,
            PrintTarget<?, ControllerConsole> printTarget,
            String id
    );

    public abstract Status queryControllerStatus();

    public abstract CrashReportStorage convertCrashReport(String raw);

    public abstract String getName();

    public String getDisplayName() {
        return getName();
    }

    public abstract String getType();

    @Override
    public String getId() {
        return getName();
    }
}
