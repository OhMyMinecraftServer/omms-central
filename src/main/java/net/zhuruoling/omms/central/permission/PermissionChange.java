package net.zhuruoling.omms.central.permission;

import java.util.List;
import java.util.Objects;

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
        GRANT, DENY, DELETE, CREATE
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, code, changes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionChange that)) return false;
        return code == that.code && operation == that.operation && Objects.equals(changes, that.changes);
    }
}
