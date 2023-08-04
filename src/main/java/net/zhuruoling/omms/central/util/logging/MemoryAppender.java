package net.zhuruoling.omms.central.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import net.zhuruoling.omms.central.GlobalVariable;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoryAppender<E extends ILoggingEvent> extends UnsynchronizedAppenderBase<E> {

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (GlobalVariable.INSTANCE.getNoGui())return;
        var res = MessageFormat.format("[{0}] [{1}/{2}] ({3}): {4}",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())
                , eventObject.getThreadName(), eventObject.getLevel().levelStr, eventObject.getLoggerName(), eventObject.getFormattedMessage());
        for (String s : res.split("\n")) {
            GlobalVariable.INSTANCE.getLogCache().add(s);
        }

    }
}
