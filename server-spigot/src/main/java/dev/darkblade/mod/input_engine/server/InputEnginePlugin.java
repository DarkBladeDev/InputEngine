package dev.darkblade.mod.input_engine.server;

import dev.darkblade.mod.input_engine.common.KeyAction;
import dev.darkblade.mod.input_engine.common.NetworkConstants;
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.ByteBuffer;

public class InputEnginePlugin extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getLogger().info("InputEngine Server Plugin is enabling...");

        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                NetworkConstants.FULL_CHANNEL,
                this
        );

        getServer().getMessenger().registerOutgoingPluginChannel(
                this,
                NetworkConstants.FULL_CHANNEL
        );

        getLogger().info("Registered messaging channel: " + NetworkConstants.FULL_CHANNEL);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(NetworkConstants.FULL_CHANNEL)) {
            return;
        }

        try {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            int actionId = buffer.getInt();
            boolean isPressed = buffer.get() != 0;

            KeyAction action = KeyAction.fromId(actionId);
            if (action == null) {
                getLogger().warning("Received unknown KeyAction ID " + actionId + " from " + player.getName());
                return;
            }

            Bukkit.getScheduler().runTask(this, () -> {
                PlayerKeyPressEvent event = new PlayerKeyPressEvent(player, action, isPressed);
                Bukkit.getPluginManager().callEvent(event);
            });

        } catch (Exception e) {
            getLogger().warning("Failed to decode keystroke payload from " + player.getName() + ": " + e.getMessage());
        }
    }
}
