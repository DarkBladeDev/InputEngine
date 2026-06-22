package dev.darkblade.mod.input_engine.server.yaml.action.impl;

import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Action that teleports the player who triggered the keybind to a specified location.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'teleport'
 *   world: 'world_nether' # Optional, uses player's current world if omitted
 *   x: 100.5
 *   y: 64.0
 *   z: -200.5
 *   yaw: 90.0 # Optional, default 0
 *   pitch: 0.0 # Optional, default 0
 * }</pre>
 */
public class TeleportAction implements ActionHandler {

    @Override
    public void execute(Player player, ConfigurationSection config) {
        String worldName = config.getString("world");
        World world;

        if (worldName == null || worldName.isBlank()) {
            world = player.getWorld();
        } else {
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                Logger logger = JavaPlugin.getProvidingPlugin(getClass()).getLogger();
                logger.warning("TeleportAction failed: World '" + worldName + "' not found.");
                return;
            }
        }

        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = (float) config.getDouble("yaw", 0.0);
        float pitch = (float) config.getDouble("pitch", 0.0);

        Location loc = new Location(world, x, y, z, yaw, pitch);
        player.teleport(loc);
    }
}
