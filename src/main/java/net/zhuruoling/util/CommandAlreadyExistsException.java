package net.zhuruoling.util;

public class CommandAlreadyExistsException extends RuntimeException{
    public CommandAlreadyExistsException(String message) {
        super(message);
    }
}
