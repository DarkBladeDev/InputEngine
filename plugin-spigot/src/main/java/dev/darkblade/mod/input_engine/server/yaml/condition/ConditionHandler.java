package dev.darkblade.mod.input_engine.server.yaml.condition;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Represents a condition that must be met before a YAML-configured keybind's actions are executed.
 *
 * <p>Implementations receive the player who triggered the keybind and the YAML configuration
 * section that defines the condition's parameters. A condition returning {@code false} will
 * prevent the associated actions from running.</p>
 *
 * <p>Example YAML usage:</p>
 * <pre>{@code
 * conditions:
 *   - type: permission
 *     permission: "myplugin.use"
 * }</pre>
 *
 * @see ConditionRegistry
 */
@FunctionalInterface
public interface ConditionHandler {

    /**
     * Checks whether the condition is met for the given player.
     *
     * @param player the player to check the condition for
     * @param config the condition configuration section from the YAML file
     * @return {@code true} if the condition is met, {@code false} otherwise
     */
    boolean check(Player player, ConfigurationSection config);
}
