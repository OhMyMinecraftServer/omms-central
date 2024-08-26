package icu.takeneko.omms.central.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MemoryAppender<E extends ILoggingEvent> extends UnsynchronizedAppenderBase<E> {
    public static final List<LogEvent> logCaches = new ArrayList<>();
    private static final List<Consumer<LogEvent>> loggingEventHandlers = new ArrayList<>();
    private static final int maxLineWidthChars = 160;

    @Override
    protected void append(E eventObject) {
        LogEvent event = LogEvent.Companion.create(eventObject);
        loggingEventHandlers.forEach(it -> it.accept(event));
        logCaches.add(event);
    }

    public static void subscribe(Consumer<LogEvent> eventHandler, boolean playback){
        loggingEventHandlers.add(eventHandler);
        if (playback) {
            logCaches.forEach(eventHandler);
        }
    }

    public static void subscribe(Consumer<LogEvent> eventHandler){
        subscribe(eventHandler, true);
    }
}
