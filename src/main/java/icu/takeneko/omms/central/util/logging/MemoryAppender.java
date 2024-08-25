package icu.takeneko.omms.central.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MemoryAppender<E extends ILoggingEvent> extends UnsynchronizedAppenderBase<E> {
    private static final List<Consumer<String>> loggingEventHandlers = new ArrayList<>();
    private static final int maxLineWidthChars = 160;

    @Override
    protected void append(E eventObject) {
        var res = MessageFormat.format(
                "[{0}] [{1}/{2}] ({3}): {4}",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),
                eventObject.getThreadName(),
                eventObject.getLevel().levelStr,
                eventObject.getLoggerName(),
                eventObject.getFormattedMessage()
        );
        loggingEventHandlers.forEach(it -> it.accept(res));
    }

    public static void subscribe(Consumer<String> eventHandler){
        loggingEventHandlers.add(eventHandler);
    }
}
