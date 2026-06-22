package dev.darkblade.mod.input_engine.server.yaml.condition.impl;

import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Checks if a player is in a specific world or one of several worlds.
 *
 * <p>YAML structure:</p>
 * <pre>{@code
 * type: world
 * world: "world_nether"
 * # OR
 * worlds:
 *   - "world"
 *   - "world_nether"
 * }</pre>
 */
public final class WorldCondition implements ConditionHandler {

    @Override
    public boolean check(Player player, ConfigurationSection config) {
        String world = config.getString("world");
        List<String> worlds = config.getStringList("worlds");

        if (world == null && worlds.isEmpty()) {
            return true;
        }

        String playerWorld = player.getWorld().getName();

        if (world != null && playerWorld.equals(world)) {
            return true;
        }

        return worlds.contains(playerWorld);
    }
}
