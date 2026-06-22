package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Action that sends an action bar message to the player who triggered the keybind.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'actionbar'
 *   text: '&aAction bar text!'
 * }</pre>
 *
 * <p>Supports color codes using {@code &}, {@code %player_name%} replacement,
 * and PlaceholderAPI placeholders when the plugin is installed.</p>
 */
public class ActionbarAction implements ActionHandler {

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

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }
}
