package net.zhuruoling.omms.central.controller;

import java.util.List;

public class CommandExecutionResult {
    String controllerId;
    String command;
    List<String> result;
    boolean status;
    String exceptionMessage;
    String exceptionDetail;

    public CommandExecutionResult(String controllerId, String command, List<String> result, boolean status, String exceptionMessage, String exceptionDetail) {
        this.controllerId = controllerId;
        this.command = command;
        this.result = result;
        this.status = status;
        this.exceptionMessage = exceptionMessage;
        this.exceptionDetail = exceptionDetail;
    }

    public String getControllerId() {
        return controllerId;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getResult() {
        return result;
    }

    public boolean getStatus() {
        return status;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionDetail() {
        return exceptionDetail;
    }
}
