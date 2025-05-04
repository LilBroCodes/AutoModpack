package org.lilbrocodes.automodpack;

import org.bukkit.plugin.java.JavaPlugin;
import org.lilbrocodes.automodpack.command.AutoModpackCommand;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.Server;

public final class AutoModpack extends JavaPlugin {
    public static final Thread hostThread = new Thread(() -> Server.main(new String[]{String.format("../%s/modpacks", dataFolder())}), "Server-Main-Thread");

    @Override
    public void onEnable() {
        new AutoModpackCommand(GlobalVariables.LOGGER).register(this, "automodpack");
        hostThread.start();
    }

    public static String dataFolder() {
        return JavaPlugin.getPlugin(AutoModpack.class).getDataFolder().toString();
    }
}
