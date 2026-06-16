package dev.darkblade.mod.input_engine.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.darkblade.mod.input_engine.common.NetworkConstants;
import dev.darkblade.mod.input_engine.server.api.KeybindData;
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class InputEnginePlugin extends JavaPlugin implements PluginMessageListener, Listener {

    private final List<KeybindData> registeredKeys = new ArrayList<>();

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
                NetworkConstants.FULL_CONFIG_CHANNEL
        );

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Registered messaging channel: " + NetworkConstants.FULL_CHANNEL);
        getLogger().info("Registered config channel: " + NetworkConstants.FULL_CONFIG_CHANNEL);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void registerExpectedKey(String actionId, int defaultKeyCode, String translationKey) {
        registerExpectedKey(actionId, defaultKeyCode, translationKey, java.util.Map.of());
    }

    public void registerExpectedKey(String actionId, int defaultKeyCode, String translationKey, java.util.Map<String, String> translations) {
        registeredKeys.add(new KeybindData(actionId, defaultKeyCode, translationKey, translations));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (registeredKeys.isEmpty()) return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeInt(registeredKeys.size());
            
            for (KeybindData data : registeredKeys) {
                byte[] idBytes = data.actionId().getBytes(StandardCharsets.UTF_8);
                out.writeInt(idBytes.length);
                out.write(idBytes);

                out.writeInt(data.defaultKey());

                byte[] transBytes = data.translationKey().getBytes(StandardCharsets.UTF_8);
                out.writeInt(transBytes.length);
                out.write(transBytes);

                out.writeInt(data.translations().size());
                for (java.util.Map.Entry<String, String> entry : data.translations().entrySet()) {
                    byte[] kBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                    out.writeInt(kBytes.length);
                    out.write(kBytes);

                    byte[] vBytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                    out.writeInt(vBytes.length);
                    out.write(vBytes);
                }
            }
            
            event.getPlayer().sendPluginMessage(this, NetworkConstants.FULL_CONFIG_CHANNEL, out.toByteArray());
        }, 20L); // Delay to ensure client is ready
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(NetworkConstants.FULL_CHANNEL)) {
            return;
        }

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            
            int idLen = in.readInt();
            byte[] idBytes = new byte[idLen];
            in.readFully(idBytes);
            String actionId = new String(idBytes, StandardCharsets.UTF_8);
            
            boolean isPressed = in.readBoolean();

            Bukkit.getScheduler().runTask(this, () -> {
                PlayerKeyPressEvent event = new PlayerKeyPressEvent(player, actionId, isPressed);
                Bukkit.getPluginManager().callEvent(event);
            });

        } catch (Exception e) {
            getLogger().warning("Failed to decode keystroke payload from " + player.getName() + ": " + e.getMessage());
        }
    }
}
