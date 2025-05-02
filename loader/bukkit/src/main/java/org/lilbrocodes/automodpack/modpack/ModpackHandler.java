package org.lilbrocodes.automodpack.modpack;

import org.lilbrocodes.automodpack.AutoModpack;
import org.lilbrocodes.automodpack.config.ConfigUtils;
import org.lilbrocodes.automodpack.wildcards.WildCards;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.lilbrocodes.automodpack.AutoModpack.LOGGER;

public class ModpackHandler {
    private static final String[] VALID_LOADERS = {
            "fabric",
            "forge",
            "neoforge"
    };
    private static Map<String, LoadedModpack> loadedModpacks = new HashMap<>();

    public static void generateModpacks() {
        loadModpacks();

        for (Map.Entry<String, LoadedModpack> modpack : loadedModpacks.entrySet()) {
            LoadedModpack loadedModpack = modpack.getValue();
            if (!Files.exists(loadedModpack.generatedPath)) {
                try {
                    createDefaultModpack(loadedModpack.generatedPath);
                } catch (IOException e) {
                    LOGGER.error("Failed to generate default modpack for modpack \n{}\n", ConfigUtils.config.getModpackName());
                }
            } else {
                loadedModpack.wildCards = new WildCards(loadedModpack.modpack.getSyncedFiles(), Set.of(loadedModpack.generatedPath));
            }
        }
    }

    public static void createDefaultModpack(Path folder) throws IOException {
        List<String> defaultDirectories = List.of(
                "mods",
                "resourcepacks",
                "saves"
        );
        for (String dir : defaultDirectories) {
            Files.createDirectories(folder.resolve(dir));
        }
    }

    public static void loadModpacks() {
        if (ConfigUtils.config.getModpacks() == null) {
            logDebug("No modpacks found in the config.");
            return;
        }

        loadedModpacks = new HashMap<>();
        for (Modpack modpack : ConfigUtils.config.getModpacks()) {
            if (!isValidModpack(modpack)) {
                continue;
            }

            if (loadedModpacks.containsKey(modpack.getLoader())) {
                logDebug("Duplicate modpack loader found: " + modpack.getLoader() + ". Skipping this modpack.");
                continue;
            }

            Path generatedPath = modpack.getPath();

            loadedModpacks.put(modpack.getLoader(), new LoadedModpack(modpack, generatedPath));
            logDebug("Loaded modpack: " + modpack.getLoader() + " -> " + generatedPath);
        }
    }

    private static boolean isValidModpack(Modpack modpack) {
        boolean isValid = true;

        if (!isValidLoader(modpack.getLoader())) {
            logDebug("Invalid modpack loader: " + modpack.getLoader());
            isValid = false;
        }

        if (!isValidFolderName(modpack.getFolder())) {
            logDebug("Invalid modpack folder: " + modpack.getFolder());
            isValid = false;
        }

        return isValid;
    }

    private static boolean isValidLoader(String loader) {
        if (loader == null) return false;
        for (String validLoader : VALID_LOADERS) {
            if (validLoader.equalsIgnoreCase(loader)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidFolderName(String folder) {
        return folder != null && !folder.isBlank() && !folder.contains("/") && !folder.contains("\\");
    }

    private static void logDebug(String message) {
        if (AutoModpack.isDebug()) {
            LOGGER.info(message);
        }
    }

    public static Map<String, LoadedModpack> getLoadedModpacks() {
        return loadedModpacks;
    }
}

