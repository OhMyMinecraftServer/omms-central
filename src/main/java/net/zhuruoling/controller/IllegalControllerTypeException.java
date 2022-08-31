package net.zhuruoling.controller;

public class IllegalControllerTypeException extends RuntimeException{
    public IllegalControllerTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
