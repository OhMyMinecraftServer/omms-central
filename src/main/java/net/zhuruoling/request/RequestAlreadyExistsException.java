package net.zhuruoling.request;

public class RequestAlreadyExistsException extends RuntimeException{
    public RequestAlreadyExistsException(String message) {
        super(message);
    }
}
