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
    }

    @EventHandler
    public void onPlayerKeyPress(PlayerKeyPressEvent event) {
        Player player = event.getPlayer();
        String actionName = event.getAction().name();
        String actionState = event.isPressed() ? "pressed" : "released";

        String message = ChatColor.GREEN + "You " + actionState + " the key for: " + ChatColor.YELLOW + actionName;
        player.sendMessage(message);

        getLogger().info(player.getName() + " " + actionState + " " + actionName);
    }
}
