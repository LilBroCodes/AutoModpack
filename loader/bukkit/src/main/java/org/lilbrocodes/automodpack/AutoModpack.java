package org.lilbrocodes.automodpack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.lilbrocodes.automodpack.command.AutoModpackCommand;
import org.lilbrocodes.automodpack.config.ConfigUtils;
import pl.skidam.automodpack_core.callbacks.IntCallback;

import java.io.IOException;
import java.util.Objects;

public final class AutoModpack extends JavaPlugin {
    public static final boolean DEBUG = false;
    public static final boolean VERBOSE_DEBUG = false;
    public static final Logger LOGGER = LogManager.getLogger("AutoModpack");

    @Override
    public void onEnable() {
        try {
            ConfigUtils.ensureFolderStructure(getDataFolder().toPath());
            AutoModpackCommand amc = new AutoModpackCommand();
            Objects.requireNonNull(getCommand("automodpack")).setExecutor(amc);
            Objects.requireNonNull(getCommand("automodpack")).setTabCompleter(amc);
        } catch (IOException e) {
            if (isDebug()) {
                LOGGER.error("Failed to initialize folder structure due to error: \"{}\", disabling...", e.toString());
            } else {
                LOGGER.error("Failed to initialize folder structure, disabling...", e.getCause());
            }
            this.getPluginLoader().disablePlugin(this);
        }
    }

    public static boolean isDebug() {
        if (ConfigUtils.config != null)
            return DEBUG || ConfigUtils.config.isDebug();
        else return
            DEBUG;
    }
}
