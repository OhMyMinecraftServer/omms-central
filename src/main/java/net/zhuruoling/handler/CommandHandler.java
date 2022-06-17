package net.zhuruoling.handler;

import net.zhuruoling.command.Command;
import net.zhuruoling.message.Message;
import net.zhuruoling.session.HandlerSession;
import net.zhuruoling.session.Session;

public abstract class CommandHandler {

    public CommandHandler(String register) {
        this.register = register;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    private String register = "";
    abstract public void handle(Command command, HandlerSession session);

}
