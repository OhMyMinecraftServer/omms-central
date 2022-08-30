package net.zhuruoling.permission;

public enum Permission {
    SERVER_OS_CONTROL,
    CENTRAL_SERVER_CONFIG,
    PERMISSION_LIST,
    PERMISSION_MODIFY,
    RUN_MCDR_COMMAND,
    RUN_MINECRAFT_COMMAND,
    START_SERVER,
    STOP_SERVER,
    WHITELIST_ADD,
    WHITELIST_REMOVE,
    WHITELIST_CREATE,
    WHITELIST_DELETE,
    ANNOUNCEMENT_CREATE,
    ANNOUNCEMENT_DELETE,
    ANNOUNCEMENT_EDIT,
    EXECUTE_PLUGIN_COMMAND
}

/*
low
group_server:
    server_os_control
    omms_configuration
    none
    none
group_minecraft_server_control:
    run_mcdr_command
    run_minecraft_command
    start_server
    stop_server
group_whitelists:
    whitelist_add
    whitelist_remove
    whitelist_create
    whitelist_delete
group_announcement:
    announcement_create
    announcement_delete
    announcement_edit
    none
high

16371: owner
 */
