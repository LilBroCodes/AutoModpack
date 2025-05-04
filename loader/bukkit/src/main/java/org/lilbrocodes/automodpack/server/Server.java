package org.lilbrocodes.automodpack.server;

import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.modpack.Modpack;
import pl.skidam.automodpack_core.modpack.ModpackContent;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;

import java.nio.file.Path;
import java.util.ArrayList;

import static pl.skidam.automodpack_core.GlobalVariables.*;

public class Server {
    public static Modpack modpack;

    public static void startServer(String workingDirectory) {
        hostServer = new NettyServer();

        Path cwd = Path.of(System.getProperty("user.dir")).resolve(workingDirectory);
        Path modpacksDir = cwd.resolve("modpacks");

        if (!modpacksDir.toFile().mkdirs()) return;

        serverConfigFile = cwd.resolve("automodpack-server.json");
        serverCoreConfigFile = cwd.resolve("automodpack-core.json");

        serverConfig = ConfigTools.load(serverConfigFile, Jsons.ServerConfigFields.class);
        if (serverConfig != null) {
            serverConfig.syncedFiles = new ArrayList<>();
            serverConfig.hostModpackOnMinecraftPort = false;
            serverConfig.validateSecrets = false;
            ConfigTools.save(serverConfigFile, serverConfig);

            if (serverConfig.hostPort == -1) {
                LOGGER.error("Host port not set in config!");
                return;
            }
        }

        Jsons.ServerCoreConfigFields serverCoreConfig = ConfigTools.load(serverCoreConfigFile, Jsons.ServerCoreConfigFields.class);
        if (serverCoreConfig != null) {
            AM_VERSION = serverCoreConfig.automodpackVersion;
            LOADER = serverCoreConfig.loader;
            LOADER_VERSION = serverCoreConfig.loaderVersion;
            MC_VERSION = serverCoreConfig.mcVersion;
            ConfigTools.save(serverCoreConfigFile, serverCoreConfig);
        }
    }
}
