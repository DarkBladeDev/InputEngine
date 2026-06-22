package dev.darkblade.mod.input_engine.server.yaml.condition.impl;

import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Checks if a player is in a specific gamemode or one of several gamemodes.
 *
 * <p>YAML structure:</p>
 * <pre>{@code
 * type: gamemode
 * gamemode: "SURVIVAL"
 * # OR
 * gamemodes:
 *   - "SURVIVAL"
 *   - "ADVENTURE"
 * }</pre>
 */
public final class GamemodeCondition implements ConditionHandler {

    @Override
    public boolean check(Player player, ConfigurationSection config) {
        String gamemode = config.getString("gamemode");
        List<String> gamemodes = config.getStringList("gamemodes");

        if (gamemode == null && gamemodes.isEmpty()) {
            return true;
        }

        String playerGamemode = player.getGameMode().name();

        if (gamemode != null && playerGamemode.equalsIgnoreCase(gamemode)) {
            return true;
        }

        for (String gm : gamemodes) {
            if (playerGamemode.equalsIgnoreCase(gm)) {
                return true;
            }
        }

        return false;
    }
}
