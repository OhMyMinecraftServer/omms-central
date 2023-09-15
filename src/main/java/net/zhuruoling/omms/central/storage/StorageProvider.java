package net.zhuruoling.omms.central.storage;

import net.zhuruoling.omms.central.announcement.Announcement;
import net.zhuruoling.omms.central.announcement.AnnouncementNotExistException;
import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.permission.PermissionCodeAlreadyExistsException;
import net.zhuruoling.omms.central.whitelist.Whitelist;
import net.zhuruoling.omms.central.whitelist.WhitelistAlreadyExistsException;
import net.zhuruoling.omms.central.whitelist.WhitelistNotExistException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface StorageProvider {

    default void loadAll() throws Exception {
        loadWhitelists();
        loadPermissions();
        loadAnnouncements();
    }

    default void saveAll() throws Exception {
        saveWhitelists();
        savePermissions();
        saveAnnouncements();
    }

    Whitelist getWhitelistByName(String name) throws WhitelistNotExistException;

    void createWhitelist(String name) throws WhitelistAlreadyExistsException;

    void loadWhitelists() throws Exception;

    void saveWhitelists() throws Exception;

    @Nullable
    List<Permission> getPermissionByCode(int code);

    default void addPermissionCode(int code) {
        addPermissionCode(code, new ArrayList<>());
    }

    void addPermissionCode(int code, List<Permission> permissions) throws PermissionCodeAlreadyExistsException;

    void loadPermissions() throws Exception;

    void savePermissions() throws Exception;

    Announcement getAnnouncementById(String id) throws AnnouncementNotExistException;
    void createAnnouncment(Announcement announcement) throws Exception;
    void loadAnnouncements() throws Exception;
    void saveAnnouncements() throws Exception;

    //
}
