package icu.takeneko.omms.central.network.session;

public class FailureReasons {
    public static final String CONTROLLER_NOT_FOUND = "controller.not_found";
    public static final String CONTROLLER_UNAUTHORISED = "controller.unauthorised";
    public static final String CONSOLE_EXISTS = "controller.console.exists";
    public static final String CONSOLE_NOT_FOUND = "controller.console.not_found";
    public static final String SERVER_INTERNAL_ERROR = "server.internal";

    public static final String PERMISSION_CHANGE_EXISTS = "permission.change_exists";

    public static final String PLAYER_EXISTS = "whitelist.player_exists";
    public static final String PLAYER_NOT_FOUND = "whitelist.player_not_found";
    public static final String WHITELIST_NOT_FOUND = "whitelist.not_found";
    public static final String WHITELIST_EXISTS = "whitelist.exists";

    public static final String RATE_EXCEED = "server.rate_exceed";
    public static final String VERSION_NOT_MATCH = "auth.version.mismatch";
    public static final String PERMISSION_DENIED = "auth.permission_denied";

}
