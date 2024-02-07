package icu.takeneko.omms.central.command;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommandSourceStack {
    Logger logger = LoggerFactory.getLogger("CommandSource");
    Source source;
    @NotNull List<String> feedbackLines = new ArrayList<>();

    public CommandSourceStack(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public enum Source{
        CONSOLE, PLUGIN, INTERNAL, REMOTE
    }

    public void sendFeedback(String feedback){
        if (source == Source.CONSOLE){
            logger.info(feedback);
        }else {
            feedbackLines.add(feedback);
        }
    }

    public void sendError(String message){
        if (source == Source.CONSOLE){
            logger.error(message);
        }else {
            feedbackLines.add("E: " + message);
        }
    }

    public @NotNull List<String> getFeedbackLines() {
        return feedbackLines;
    }
}
