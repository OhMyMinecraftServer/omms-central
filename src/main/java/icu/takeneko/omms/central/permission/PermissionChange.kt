package icu.takeneko.omms.central.permission

import java.util.*

class PermissionChange(
    var operation: Operation,
    var name: String,
    var changes: List<Permission>
) {

    override fun hashCode(): Int {
        return Objects.hash(operation, name, changes)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other !is PermissionChange) false else name == other.name && operation == other.operation && changes == other.changes
    }

    override fun toString(): String {
        val changes = changes.joinToString(", ")
        return when (operation) {
            Operation.GRANT -> {
                "Grant those permissions: [$changes] to permission name $name"
            }

            Operation.DENY -> {
                "Deny those permissions: [$changes] from permission name $name"
            }

            Operation.DELETE -> {
                "Delete permission name $name"
            }

            Operation.CREATE -> {
                "Create permission name $name with default permissions: [$changes]"
            }
        }
    }
}
