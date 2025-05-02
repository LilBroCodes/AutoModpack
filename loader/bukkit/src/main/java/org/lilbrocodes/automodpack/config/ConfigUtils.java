package org.lilbrocodes.automodpack.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lilbrocodes.automodpack.AutoModpack;
import org.lilbrocodes.automodpack.modpack.ModpackHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lilbrocodes.automodpack.AutoModpack.LOGGER;

public class ConfigUtils {
    public static Path dataFolder;
    public static Path modpacksFolder;
    public static Config config;

    public static void setDataFolder(Path path) {
        dataFolder = path;
        modpacksFolder = dataFolder.resolve("modpacks");
    }

    public static void ensureFolderStructure(Path path) throws IOException {
        setDataFolder(path);

        if (!Files.exists(modpacksFolder)) {
            Files.createDirectories(modpacksFolder);
        }

        Path configPath = dataFolder.resolve("config.json");
        if (!Files.exists(configPath)) {
            Config config = Config.getDefault();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonContent = gson.toJson(config);

            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                writer.write(jsonContent);
            }
        }

        reload();
        if (config.isGenerateModpacksOnStart()) ModpackHandler.generateModpacks();
    }

    public static void reload() {
        try {
            config = Config.readFromFile(dataFolder.resolve("config.json").toFile());
        } catch (IOException e) {
            if (AutoModpack.DEBUG) {
                LOGGER.error("Failed to reload config because of error: \"{}\"", e.toString());
            } else {
                LOGGER.error("Failed to reload config!");
            }
        }
    }
}
