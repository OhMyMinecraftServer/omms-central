package net.zhuruoling.handler;

import net.zhuruoling.command.Command;
import net.zhuruoling.plugins.PluginManager;
import net.zhuruoling.plugins.RequestServerInterface;
import net.zhuruoling.session.HandlerSession;

import java.util.Objects;

public class PluginCommandHandler implements CommandHandler {
    private final String pluginName;
    private final String code;
    private final String funcName;

    public PluginCommandHandler(String pluginName, String code, String funcName) {
        this.pluginName = pluginName;
        this.code = code;
        this.funcName = funcName;
    }

    @Override
    public void handle(Command command, HandlerSession session) {
        if (!Objects.equals(command.getCmd(), code)){
            throw new UnsupportedOperationException("The operation code defined in this class does not align with requested operation code.");
        }
        PluginManager.INSTANCE.execute(pluginName, funcName, command, new RequestServerInterface(session));
    }
}
