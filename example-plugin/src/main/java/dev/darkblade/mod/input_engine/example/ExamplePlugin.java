package dev.darkblade.mod.input_engine.example;

import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("InputEngine Example Plugin is enabling...");
        getServer().getPluginManager().registerEvents(this, this);

        // Register a test key
        dev.darkblade.mod.input_engine.server.InputEnginePlugin inputPlugin = 
            (dev.darkblade.mod.input_engine.server.InputEnginePlugin) getServer().getPluginManager().getPlugin("InputEngine");
        
        if (inputPlugin != null) {
            // Dash key (V) - Tracks hold duration and double tap, no modifiers required
            inputPlugin.registerExpectedKey(
                "example:dash", 86, "key.example.dash", java.util.Map.of(
                    "en_us", "Dash",
                    "es_es", "Embestida"
                ), false, false, false, true, true, true
            );

            // Another key (A) to trigger the combo
            inputPlugin.registerExpectedKey(
                "example:attack", 65, "key.example.attack", java.util.Map.of(
                    "en_us", "Special Attack",
                    "es_es", "Ataque Especial"
                ), false, false, false, false, false, true
            );
            
            // Register a combo A -> Dash (example:attack -> example:dash)
            inputPlugin.getComboManager().registerCombo("example:super_dash", java.util.List.of("example:attack", "example:dash"));
        }
    }

    @EventHandler
    public void onPlayerKeyPress(PlayerKeyPressEvent event) {
        Player player = event.getPlayer();
        String actionName = event.getActionId();
        
        if (event.isDoubleTap()) {
            player.sendMessage(ChatColor.GOLD + "Double tapped " + actionName + "!");
        } else if (!event.isPressed() && event.getHoldDurationMs() > 0) {
            player.sendMessage(ChatColor.AQUA + "Held " + actionName + " for " + event.getHoldDurationMs() + "ms");
        } else if (event.isPressed()) {
            player.sendMessage(ChatColor.GREEN + "Pressed " + actionName);
            
            // Test Cooldown
            if (actionName.equals("example:dash")) {
                dev.darkblade.mod.input_engine.server.api.InputEngineAPI.sendCooldown(player, "example:dash", 2000);
            }
        }
    }

    @EventHandler
    public void onPlayerCombo(dev.darkblade.mod.input_engine.server.api.PlayerComboEvent event) {
        event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Combo triggered: " + event.getComboId());
    }
}
