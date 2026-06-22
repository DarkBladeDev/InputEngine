package dev.darkblade.mod.input_engine.server.command;

import dev.darkblade.mod.input_engine.server.InputEnginePlugin;
import dev.darkblade.mod.input_engine.server.yaml.YamlKeyManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InputEngineCommand implements CommandExecutor, TabCompleter {

    private final InputEnginePlugin plugin;
    private final YamlKeyManager yamlKeyManager;

    public InputEngineCommand(InputEnginePlugin plugin, YamlKeyManager yamlKeyManager) {
        this.plugin = plugin;
        this.yamlKeyManager = yamlKeyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("inputengine.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "Usage: /inputengine reload");
            return true;
        }

        // Reload config.yml
        plugin.reloadConfig();
        
        // Clear existing registered keys
        plugin.clearRegisteredKeys();
        
        // Reload YAML keys
        yamlKeyManager.reloadKeys();

        sender.sendMessage(ChatColor.GREEN + "InputEngine keybinds reloaded successfully!");
        sender.sendMessage(ChatColor.GRAY + "Loaded " + yamlKeyManager.getAllKeybinds().size() + " keybinds.");

        // Check if we should sync to online players
        if (plugin.getConfig().getBoolean("auto-sync-on-reload", true)) {
            plugin.syncToOnlinePlayers();
            sender.sendMessage(ChatColor.GREEN + "Keybinds synced to all online players.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Auto-sync is disabled. Players must reconnect to receive updates.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("inputengine.admin")) {
            List<String> completions = new ArrayList<>();
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
