package icu.takeneko.omms.central.storage

import icu.takeneko.omms.central.announcement.Announcement
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.whitelist.Whitelist

class StorageProviderImpl : StorageProvider {
    override fun getWhitelistByName(name: String?): Whitelist {
        TODO("Not yet implemented")
    }

    override fun createWhitelist(name: String?) {
        TODO("Not yet implemented")
    }

    override fun loadWhitelists() {
        TODO("Not yet implemented")
    }

    override fun saveWhitelists() {
        TODO("Not yet implemented")
    }

    override fun getPermissionByCode(code: Int): MutableList<Permission>? {
        TODO("Not yet implemented")
    }

    override fun addPermissionCode(code: Int, permissions: MutableList<Permission>?) {
        TODO("Not yet implemented")
    }

    override fun loadPermissions() {
        TODO("Not yet implemented")
    }

    override fun savePermissions() {
        TODO("Not yet implemented")
    }

    override fun getAnnouncementById(id: String?): Announcement {
        TODO("Not yet implemented")
    }

    override fun createAnnouncment(announcement: Announcement?) {
        TODO("Not yet implemented")
    }

    override fun loadAnnouncements() {
        TODO("Not yet implemented")
    }

    override fun saveAnnouncements() {
        TODO("Not yet implemented")
    }

}
