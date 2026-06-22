package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Action that sends a formatted message to the player who triggered the keybind.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'message'
 *   text: '&aYou pressed the keybind, %player_name%!'
 * }</pre>
 *
 * <p>Supports color codes using {@code &}, {@code %player_name%} replacement,
 * and PlaceholderAPI placeholders when the plugin is installed.</p>
 */
public class MessageAction implements ActionHandler {

    @Override
    public void execute(Player player, ConfigurationSection config) {
        String text = config.getString("text");
        if (text == null || text.isBlank()) {
            return;
        }

        text = text.replace("%player_name%", player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }

        text = ChatColor.translateAlternateColorCodes('&', text);

        player.sendMessage(text);
    }
}
