package icu.takeneko.omms.central.permission

import java.util.*

class PermissionChange(
    @JvmField var operation: Operation,
    @JvmField var code: Int,
    @JvmField var changes: List<Permission>
) {

    override fun hashCode(): Int {
        return Objects.hash(operation, code, changes)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        return if (o !is PermissionChange) false else code == o.code && operation == o.operation && changes == o.changes
    }

    override fun toString(): String {
        val changes = changes.joinToString(", ")
        return when (operation) {
            Operation.GRANT -> {
                "Grant those permissions: [$changes] to permission code $code"
            }

            Operation.DENY -> {
                "Deny those permissions: [$changes] from permission code $code"
            }

            Operation.DELETE -> {
                "Delete permission code $code"
            }

            Operation.CREATE -> {
                "Create permission code $code with default permissions: [$changes]"
            }
        }
    }
}
