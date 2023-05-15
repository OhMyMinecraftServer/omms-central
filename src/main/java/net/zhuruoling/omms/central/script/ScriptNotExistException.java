package net.zhuruoling.omms.central.script;

public class ScriptNotExistException extends RuntimeException{
    public ScriptNotExistException(String message) {
        super(message);
    }
}
