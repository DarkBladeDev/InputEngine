package dev.darkblade.mod.input_engine.server.yaml.condition.impl;

import dev.darkblade.mod.input_engine.server.InputEnginePlugin;
import dev.darkblade.mod.input_engine.server.api.InputEngineAPI;
import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Checks if a keybind is on cooldown for a player.
 *
 * <p>YAML structure:</p>
 * <pre>{@code
 * type: cooldown
 * id: "my_keybind_cooldown"
 * duration: 5000 # in milliseconds
 * message: "You must wait %time%s!" # Optional
 * }</pre>
 */
public final class CooldownCondition implements ConditionHandler {

    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    @Override
    public boolean check(Player player, ConfigurationSection config) {
        String id = config.getString("id");
        if (id == null || id.isEmpty()) {
            JavaPlugin.getPlugin(InputEnginePlugin.class).getLogger().warning("Cooldown condition missing required 'id' parameter.");
            return true;
        }

        long duration = config.getLong("duration", 0L);
        String message = config.getString("message");

        UUID uuid = player.getUniqueId();
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(uuid, k -> new HashMap<>());

        long currentTime = System.currentTimeMillis();
        Long expiryTime = playerCooldowns.get(id);

        if (expiryTime != null && currentTime < expiryTime) {
            long remainingMs = expiryTime - currentTime;
            if (message != null) {
                double remainingSeconds = remainingMs / 1000.0;
                String timeStr = String.format(Locale.US, "%.1f", remainingSeconds);
                player.sendMessage(message.replace("%time%", timeStr));
            }
            InputEngineAPI.sendCooldown(player, id, (int) remainingMs);
            return false;
        }

        playerCooldowns.put(id, currentTime + duration);
        InputEngineAPI.sendCooldown(player, id, (int) duration);
        return true;
    }
}
