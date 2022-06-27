package net.zhuruoling.system;

import java.util.List;

public class DirectoryInfo {
    List<String> folders = null;
    List<String> files = null;

    SystemResult result = SystemResult.__NULL;

    public DirectoryInfo() {
    }

    public SystemResult getResult() {
        return result;
    }

    public void setResult(SystemResult result) {
        this.result = result;
    }

    public DirectoryInfo(List<String> folders, List<String> files) {
        this.folders = folders;
        this.files = files;
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
