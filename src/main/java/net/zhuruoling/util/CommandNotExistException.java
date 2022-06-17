package net.zhuruoling.util;

public class CommandNotExistException extends RuntimeException{
    public CommandNotExistException(String message) {
        super(message);
    }
}
