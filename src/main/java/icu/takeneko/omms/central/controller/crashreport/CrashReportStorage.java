package icu.takeneko.omms.central.controller.crashreport;

import java.util.List;

public record CrashReportStorage(String controllerId, Long timeMillis, List<String> content) { }

