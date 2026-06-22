package dev.darkblade.mod.input_engine.server.yaml.condition.impl;

import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionHandler;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Condition that checks whether the player has a specific permission node.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'permission'
 *   permission: 'myserver.vip'
 * }</pre>
 */
public class PermissionCondition implements ConditionHandler {

    @Override
    public boolean check(Player player, ConfigurationSection config) {
        String permission = config.getString("permission");
        if (permission == null || permission.isBlank()) {
            return true; // No permission specified — pass by default
        }
        return player.hasPermission(permission);
    }
}
