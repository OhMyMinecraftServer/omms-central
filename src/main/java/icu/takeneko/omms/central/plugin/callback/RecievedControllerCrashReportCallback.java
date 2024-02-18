package icu.takeneko.omms.central.plugin.callback;

import icu.takeneko.omms.central.controller.crashreport.CrashReportStorage;

import java.util.function.Consumer;

public class RecievedControllerCrashReportCallback extends Callback<CrashReportStorage> {

    public static RecievedControllerCrashReportCallback INSTANCE = new RecievedControllerCrashReportCallback();

    @Override
    public void register(Consumer<CrashReportStorage> consumer) {
        this.consumers.add(consumer);
    }
}
