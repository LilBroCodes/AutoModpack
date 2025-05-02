package org.lilbrocodes.automodpack.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lilbrocodes.automodpack.config.ConfigUtils;
import org.lilbrocodes.automodpack.modpack.ModpackHandler;
import org.lilbrocodes.automodpack.modpack.LoadedModpack;
import org.lilbrocodes.automodpack.utils.PathUtil;
import org.lilbrocodes.automodpack.utils.TreeIO;

import java.util.List;
import java.util.Map;

import static org.lilbrocodes.automodpack.AutoModpack.isDebug;

public class AutoModpackCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("info")) {
            commandSender.sendMessage(affixMessage(String.format("%d modpacks are loaded:", ModpackHandler.getLoadedModpacks().size())));

            for (Map.Entry<String, LoadedModpack> entry : ModpackHandler.getLoadedModpacks().entrySet()) {
                String loader = entry.getKey();
                LoadedModpack modpack = entry.getValue();

                commandSender.sendMessage(affixMessage(String.format("§6%s§r (Folder: %s)",
                        loader, modpack.modpack.getFolder())));

                if (!modpack.wildCards.wildcardMatches.isEmpty()) {
                    if (isDebug()) {
                        List<String> files = PathUtil.cleanPaths(modpack.wildCards.wildcardMatches.keySet().stream().toList(), modpack.modpack.getPath().toString());
                        String tree = TreeIO.printTree(files, !ConfigUtils.config.useNonAscii(), PathUtil.deepestFolder(modpack.modpack.getPath().toString()));
                        commandSender.sendMessage(affixMessage(String.format("§rSynced files:§r\n%s", tree)));
                    } else {
                        commandSender.sendMessage(affixMessage(String.format("§rThere are §a%d§r Synced files§r", modpack.wildCards.wildcardMatches.size())));
                    }
                }
            }
        } else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            ConfigUtils.reload();
            commandSender.sendMessage(affixMessage("Reloaded config!"));
        } else if (args.length > 0 && args[0].equalsIgnoreCase("generate")) {
            ModpackHandler.generateModpacks();
            commandSender.sendMessage(affixMessage("Generating modpack..."));
        } else {
            commandSender.sendMessage(affixMessage("§cInvalid subcommand!§r"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return args.length == 1 ? List.of("info", "reload", "generate") : List.of();
    }

    private String affixMessage(String message) {
        return String.format("[§aAutoModpack§r] %s", message);
    }
}
