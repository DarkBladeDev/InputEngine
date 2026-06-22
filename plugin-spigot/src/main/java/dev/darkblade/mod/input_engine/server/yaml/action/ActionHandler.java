package dev.darkblade.mod.input_engine.server.yaml.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Represents an executable action that can be triggered by a YAML-configured keybind.
 *
 * <p>Implementations receive the player who triggered the keybind and the action
 * configuration section from the YAML file, allowing each action type to read
 * its own specific parameters.</p>
 *
 * <p>Example YAML configuration:</p>
 * <pre>{@code
 * actions:
 *   - type: player_command
 *     command: "/spawn"
 *   - type: message
 *     text: "&aYou pressed the keybind!"
 * }</pre>
 *
 * @see ActionRegistry
 */
@FunctionalInterface
public interface ActionHandler {

    /**
     * Executes the action for the given player.
     *
     * @param player the player who triggered the keybind
     * @param config the action configuration section from the YAML file
     */
    void execute(Player player, ConfigurationSection config);
}
