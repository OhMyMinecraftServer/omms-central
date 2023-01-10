package net.zhuruoling.omms.central.network.session.request;

public class RequestAlreadyExistsException extends RuntimeException{
    public RequestAlreadyExistsException(String message) {
        super(message);
    }
}
