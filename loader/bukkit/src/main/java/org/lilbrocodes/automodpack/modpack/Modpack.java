package org.lilbrocodes.automodpack.modpack;

import org.lilbrocodes.automodpack.config.ConfigUtils;

import java.nio.file.Path;
import java.util.List;

public class Modpack {
    public String loader;
    public String loader_version;
    public String folder;
    public List<String> syncedFiles;
    public List<String> allowEditsInFiles;

    public String getLoader() {
        return loader;
    }

    public String getLoaderVersion() {
        return loader_version;
    }

    public void setLoader(String loader) {
        this.loader = loader;
    }

    public void setLoaderVersion(String version) {
        this.loader_version = version;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public List<String> getSyncedFiles() {
        return syncedFiles;
    }

    public void setSyncedFiles(List<String> syncedFiles) {
        this.syncedFiles = syncedFiles;
    }

    public void setAllowEditsInFiles(List<String> allowEditsInFiles) {
        this.allowEditsInFiles = allowEditsInFiles;
    }

    public Path getPath() {
        return ConfigUtils.modpacksFolder.resolve(folder.equals("auto") ? loader : folder);
    }
}
