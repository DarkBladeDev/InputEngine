package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Action that plays a sound for the player who triggered the keybind.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'sound'
 *   sound: 'entity.experience_orb.pickup'
 *   volume: 1.0
 *   pitch: 1.0
 * }</pre>
 */
public class SoundAction implements ActionHandler {

    @Override
    public void execute(Player player, ConfigurationSection config) {
        String sound = config.getString("sound");
        if (sound == null || sound.isBlank()) {
            return;
        }

        double volume = config.getDouble("volume", 1.0);
        double pitch = config.getDouble("pitch", 1.0);

        player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
    }
}
