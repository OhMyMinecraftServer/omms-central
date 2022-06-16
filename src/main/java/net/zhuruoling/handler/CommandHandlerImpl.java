package net.zhuruoling.handler;

import net.zhuruoling.command.Command;
import net.zhuruoling.message.Message;
import net.zhuruoling.session.HandlerSession;

public class CommandHandlerImpl implements CommandHandler{
    @Override
    public void handle(Command command, HandlerSession session) {
        switch (command.getCmd()){
            default -> {

            }
        }
    }


}
