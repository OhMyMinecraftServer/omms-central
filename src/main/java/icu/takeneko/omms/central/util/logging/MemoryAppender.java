package icu.takeneko.omms.central.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import icu.takeneko.omms.central.RunConfiguration;
import icu.takeneko.omms.central.SharedObjects;
import icu.takeneko.omms.central.graphics.GuiMainKt;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoryAppender<E extends ILoggingEvent> extends UnsynchronizedAppenderBase<E> {

    private static final int maxLineWidthChars = 160;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (RunConfiguration.INSTANCE.getNoGui()) return;
        var res = MessageFormat.format("[{0}] [{1}/{2}] ({3}): {4}",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())
                , eventObject.getThreadName(), eventObject.getLevel().levelStr, eventObject.getLoggerName(), eventObject.getFormattedMessage());
        for (String s : res.split("\n")) {
            var len = s.length();
            var beginIndex = 0;
            while (len > maxLineWidthChars) {
                SharedObjects.INSTANCE.getLogCache().add(s.substring(beginIndex, beginIndex + maxLineWidthChars));
                GuiMainKt.view.scrollToEnd();
                len -= maxLineWidthChars;
                beginIndex += maxLineWidthChars;
            }
            SharedObjects.INSTANCE.getLogCache().add(s.substring(beginIndex));
        }

    }
}
