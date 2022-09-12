package net.zhuruoling.network.session.request;

public class RequestAlreadyExistsException extends RuntimeException{
    public RequestAlreadyExistsException(String message) {
        super(message);
    }
}
