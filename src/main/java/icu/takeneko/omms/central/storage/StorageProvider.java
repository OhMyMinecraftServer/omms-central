package icu.takeneko.omms.central.storage;

import icu.takeneko.omms.central.announcement.Announcement;
import icu.takeneko.omms.central.announcement.AnnouncementNotExistException;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.permission.PermissionCodeAlreadyExistsException;
import icu.takeneko.omms.central.whitelist.Whitelist;
import icu.takeneko.omms.central.whitelist.WhitelistAlreadyExistsException;
import icu.takeneko.omms.central.whitelist.WhitelistNotExistException;
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
