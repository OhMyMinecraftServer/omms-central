package net.zhuruoling.handler;

import net.zhuruoling.command.Command;
import net.zhuruoling.message.Message;
import net.zhuruoling.session.HandlerSession;
import net.zhuruoling.session.Session;

public interface CommandHandler {
    public void handle(Command command, HandlerSession session);
}
