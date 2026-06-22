package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Action that sends a title and subtitle to the player who triggered the keybind.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'title'
 *   title: '&6Big Title'
 *   subtitle: '&eSmaller subtitle'
 *   fade_in: 10
 *   stay: 70
 *   fade_out: 20
 * }</pre>
 *
 * <p>Supports color codes using {@code &}, {@code %player_name%} replacement,
 * and PlaceholderAPI placeholders when the plugin is installed.</p>
 */
public class TitleAction implements ActionHandler {

    @Override
    public void execute(Player player, ConfigurationSection config) {
        String title = config.getString("title");
        String subtitle = config.getString("subtitle");

        if (title == null) title = "";
        if (subtitle == null) subtitle = "";

        if (title.isBlank() && subtitle.isBlank()) {
            return;
        }

        title = title.replace("%player_name%", player.getName());
        subtitle = subtitle.replace("%player_name%", player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            title = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, title);
            subtitle = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, subtitle);
        }

        title = ChatColor.translateAlternateColorCodes('&', title);
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

        int fadeIn = config.getInt("fade_in", 10);
        int stay = config.getInt("stay", 70);
        int fadeOut = config.getInt("fade_out", 20);

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
