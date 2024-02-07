package icu.takeneko.omms.central.permission;

public class IllegalPermissionNameException extends RuntimeException{
    public IllegalPermissionNameException(String message) {
        super(message);
    }

    public IllegalPermissionNameException(Throwable cause) {
        super(cause);
    }

    public IllegalPermissionNameException(String permissionName, Throwable cause){
        super("%s is not a valid permission name.".formatted(permissionName), cause);
    }
}
