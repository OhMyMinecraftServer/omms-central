package net.zhuruoling.omms.central.system;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DirectoryInfo {
    @Nullable List<String> folders = null;
    @Nullable List<String> files = null;

    SystemResult result = SystemResult.__NULL;

    public DirectoryInfo() {
    }

    public DirectoryInfo(List<String> folders, List<String> files) {
        this.folders = folders;
        this.files = files;
    }

    public SystemResult getResult() {
        return result;
    }

    public void setResult(SystemResult result) {
        this.result = result;
    }

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
