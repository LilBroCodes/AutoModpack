package org.lilbrocodes.automodpack.modpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lilbrocodes.automodpack.config.ConfigUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class ModpackMeta {
    private String modpackName;
    private String automodpackVersion;
    private String loader;
    private String loaderVersion;
    private String mcVersion;
    private List<ModFile> list;

    public static ModpackMeta readFromFile(File file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, ModpackMeta.class);
        }
    }

    public static class ModFile {
        public String file;
        public String size;
        public String type;
        public boolean editable;
        public String sha1;
        public String murmur;

        public static ModFile fromFile(Path file, List<String> allowEditsInFiles) {
            ModFile modFile = new ModFile();

            return modFile;
        }
    }

    public static ModpackMeta fromModpack(LoadedModpack modpack) throws IOException {
        ModpackMeta meta = new ModpackMeta();
        meta.modpackName = ConfigUtils.config.getModpackName();
        meta.automodpackVersion = "4.0.0-beta29"; // TODO: Figure out a better way to do this, for now this has to just be hardcoded
        meta.loader = modpack.modpack.loader;
        meta.loaderVersion = modpack.modpack.loader_version;
        // TODO: MC VERSION
        meta.list = new ArrayList<>();
        modpack.wildCards.wildcardMatches.values().forEach(file -> meta.list.add(ModFile.fromFile(file, modpack.modpack.allowEditsInFiles)));
        return meta;
    }
}
