package org.lilbrocodes.automodpack.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lilbrocodes.automodpack.modpack.Modpack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Config {
    private boolean generateModpacksOnStart;
    private boolean debug;
    private boolean useNonAscii;
    private String modpackName;
    private List<Modpack> modpacks;

    public boolean isGenerateModpacksOnStart() {
        return generateModpacksOnStart;
    }

    public void setGenerateModpacksOnStart(boolean generateModpacksOnStart) {
        this.generateModpacksOnStart = generateModpacksOnStart;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean useNonAscii() {
        return useNonAscii;
    }

    public String getModpackName() {
        return modpackName;
    }

    public void setModpackName(String modpackName) {
        this.modpackName = modpackName;
    }

    public List<Modpack> getModpacks() {
        return modpacks;
    }

    public void setModpacks(List<Modpack> modpacks) {
        this.modpacks = modpacks;
    }

    public static Config getDefault() {
        Config config = new Config();
        config.setGenerateModpacksOnStart(true);
        config.setModpackName("");
        config.useNonAscii = false;
        config.debug = false;

        Modpack templateModpack = new Modpack();
        templateModpack.setFolder("auto");
        templateModpack.setLoader("fabric");
        templateModpack.setLoaderVersion("0.15.11");
        templateModpack.setSyncedFiles(Arrays.asList(
                "/mods/*.jar",
                "!/mods/iDontWantThisModInModpack.jar",
                "!/config/andThisConfigToo.json",
                "!/mods/andAllTheseMods-*.jar",
                "!/mods/server-*.jar"
        ));
        templateModpack.setAllowEditsInFiles(Arrays.asList(
                "/options.txt",
                "/config/*",
                "!/config/excludeThisFile"
        ));
        config.setModpacks(List.of(templateModpack));
        return config;
    }

    public static Config readFromFile(File file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Config.class);
        }
    }
}
