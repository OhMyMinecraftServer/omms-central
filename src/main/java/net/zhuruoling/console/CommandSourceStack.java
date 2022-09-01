package net.zhuruoling.console;

public class CommandSourceStack {
    //pass
    Source source;

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
}
