package org.lilbrocodes.automodpack.command;

import org.apache.logging.log4j.Logger;
import org.lilbrocodes.commander.api.CommanderCommand;
import org.lilbrocodes.commander.api.executor.ExecutorNode;
import org.lilbrocodes.commander.api.executor.ParameterExecutorNode;
import org.lilbrocodes.commander.api.executor.ParentExecutorNode;
import org.lilbrocodes.commander.api.executor.PseudoExecutorNode;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static pl.skidam.automodpack_core.GlobalVariables.*;

public class AutoModpackCommand extends CommanderCommand {
    private final Logger logger;

    public AutoModpackCommand(Logger logger) {
        super(new PseudoExecutorNode("automodpack", "Root command", "§aAutoModpack", (sender, args) -> {
            sender.sendMessage(affix("AutoModpack is loaded i guess"));
            sender.sendMessage(affix("Idk what to put here /shrug"));
            // TODO: Fix this
        }), true);
        this.logger = logger;
    }

    @Override
    public void initialize(ExecutorNode<ParentExecutorNode> rootNode) {
        if (!(rootNode instanceof ParentExecutorNode root)) return;

        ParameterExecutorNode generate = new ParameterExecutorNode(
                "generate",
                "Generate modpack.",
                "§aAutoModpack",
                List.of(),
                (sender, args) -> {
                    sender.sendMessage(affix("Starting generation..."));
//                    if (Server.modpack != null) Server.modpack.generateNew();
                    sender.sendMessage(affix("Modpack generated."));
                }
        );

        PseudoExecutorNode host = new PseudoExecutorNode(
                "host",
                "Status of modpack hosting.",
                "§AutoModpack",
                (sender, args) -> {
                    sender.sendMessage(affix(String.format("Host server %s running.", hostServer.isRunning() ? "is" : "is not")));
                }
        );

        ParentExecutorNode config = new ParentExecutorNode(
                "config",
                "Configuration options.",
                "§AutoModpack"
        );

        ParameterExecutorNode hostStart = new ParameterExecutorNode(
                "start",
                "Start modpack hosting.",
                "§aAutoModpack",
                List.of(),
                (sender, args) -> {
                    if (hostServer.isRunning()) {
                        sender.sendMessage(affix("Modpack host is already running."));
                    } else {
                        sender.sendMessage(affix("§7(1)§r Starting modpack host..."));
                        hostServer.start();
                        sender.sendMessage(affix("§7(2)§r Modpack host started."));
                    }
                }
        );

        ParameterExecutorNode hostStop = new ParameterExecutorNode(
                "stop",
                "Stop modpack hosting.",
                "§aAutoModpack",
                List.of(),
                (sender, args) -> {
                    if (!hostServer.isRunning()) {
                        sender.sendMessage(affix("Modpack host is already stopped."));
                    } else {
                        sender.sendMessage(affix("§7(1)§r Stopping modpack host..."));
                        hostServer.stop();
                        sender.sendMessage(affix("§7(2)§r Modpack host stopped."));
                    }
                }
        );

        ParameterExecutorNode hostRestart = new ParameterExecutorNode(
                "restart",
                "Restart modpack hosting.",
                "§aAutoModpack",
                List.of(),
                (sender, args) -> {
                    if (hostServer.isRunning()) {
                        sender.sendMessage(affix("§7(0)§r Stopping modpack host..."));
                        hostServer.stop();
                    }
                    sender.sendMessage(affix("§7(1)§r Starting modpack host..."));
                    hostServer.start();
                    sender.sendMessage(affix("§7(2)§r Modpack host restarted."));
                }
        );

        ParameterExecutorNode hostConnections = new ParameterExecutorNode(
                "connections",
                "Lists all currently active modpack host connections.",
                "§aAutoModpack",
                List.of(),
                (sender, args) -> sender.sendMessage(affix(String.format("%d active connections. (%s)", hostServer.getConnections().size(), join(Arrays.asList(hostServer.getConnections().keySet().toArray())))))
        );

        ParameterExecutorNode configReload = new ParameterExecutorNode(
                "reload",
                "Reload config files.",
                "§aAutoModpack",
                List.of(),
                (sender, args) -> {
                    sender.sendMessage(affix("Reloading config files..."));
                    serverConfig = ConfigTools.load(serverConfigFile, Jsons.ServerConfigFields.class);
                    Jsons.ServerCoreConfigFields serverCoreConfig = ConfigTools.load(serverCoreConfigFile, Jsons.ServerCoreConfigFields.class);
                    if (serverCoreConfig != null) {
                        AM_VERSION = serverCoreConfig.automodpackVersion;
                        LOADER = serverCoreConfig.loader;
                        LOADER_VERSION = serverCoreConfig.loaderVersion;
                        MC_VERSION = serverCoreConfig.mcVersion;
                        ConfigTools.save(serverCoreConfigFile, serverCoreConfig);
                    }
                    sender.sendMessage(affix("Reloaded config files."));
                }
        );

        host.addChild(hostStart);
        host.addChild(hostStop);
        host.addChild(hostRestart);
        host.addChild(hostConnections);

        config.addChild(configReload);

        root.addChild(generate);
        root.addChild(host);
        root.addChild(config);
        logger.info("Registered {} subcommand(s).", root.getChildren().size());
    }

    private static String affix(String message) {
        return String.format("§r§7[§aAutoModpack§7]§r %s", message);
    }

    private static String join(List<Object> list) {
        StringBuilder out = new StringBuilder();
        for (Object item : list) {
            out.append(Objects.equals(getLast(list), item) ? item.toString() : String.format("%s%s", item.toString(), ", "));
        }
        return out.toString();
    }

    @Nullable private static <T> T getLast(List<T> list) {
        if (list.isEmpty()) return null;
        else return list.get(list.size() - 1);
    }
}
