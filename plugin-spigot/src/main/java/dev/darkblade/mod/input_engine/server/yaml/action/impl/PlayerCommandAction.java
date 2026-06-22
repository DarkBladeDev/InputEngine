package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Action that executes a command as the player who triggered the keybind.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'player_command'
 *   command: 'shop open main'
 * }</pre>
 *
 * <p>Supports {@code %player_name%} replacement and PlaceholderAPI placeholders
 * when the PlaceholderAPI plugin is installed.</p>
 */
public class PlayerCommandAction implements ActionHandler {

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

        player.performCommand(command);
    }
}
