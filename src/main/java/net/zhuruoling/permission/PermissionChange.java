package net.zhuruoling.permission;

import java.util.List;

public class PermissionChange {
    private Operation operation;
    private int code;
    private List<Permission> changes;
    public PermissionChange(Operation operation, int code, List<Permission> changes) {
        this.operation = operation;
        this.code = code;
        this.changes = changes;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Permission> getChanges() {
        return changes;
    }

    public void setChanges(List<Permission> changes) {
        this.changes = changes;
    }

    public enum Operation {
        ADD, REMOVE, DELETE, CREATE
    }
}
