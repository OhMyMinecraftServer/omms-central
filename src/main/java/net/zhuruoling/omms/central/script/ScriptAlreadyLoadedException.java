package net.zhuruoling.omms.central.script;

public class ScriptAlreadyLoadedException extends RuntimeException{
    public ScriptAlreadyLoadedException(String message) {
        super(message);
    }
}
