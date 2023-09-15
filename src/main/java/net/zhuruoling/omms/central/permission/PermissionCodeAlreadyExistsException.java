package net.zhuruoling.omms.central.permission;

public class PermissionCodeAlreadyExistsException extends RuntimeException {
    int code;

    public PermissionCodeAlreadyExistsException(int code) {
        super(String.valueOf(code));
        this.code = code;
    }

    public PermissionCodeAlreadyExistsException(Throwable cause, int code) {
        super(String.valueOf(code), cause);
        this.code = code;
    }
}
