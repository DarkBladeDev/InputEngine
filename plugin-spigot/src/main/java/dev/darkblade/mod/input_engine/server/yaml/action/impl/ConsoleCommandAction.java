package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Action that executes a command as the server console.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'console_command'
 *   command: 'eco give %player_name% 100'
 * }</pre>
 *
 * <p>Supports {@code %player_name%} replacement and PlaceholderAPI placeholders
 * when the PlaceholderAPI plugin is installed.</p>
 */
public class ConsoleCommandAction implements ActionHandler {

    @Override
    public void execute(Player player, ConfigurationSection config) {
        String command = config.getString("command");
        if (command == null || command.isBlank()) {
            return;
        }

        command = command.replace("%player_name%", player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            command = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, command);
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
