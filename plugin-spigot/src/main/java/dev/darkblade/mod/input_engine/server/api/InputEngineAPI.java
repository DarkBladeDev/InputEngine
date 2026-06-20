package dev.darkblade.mod.input_engine.server.api;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import dev.darkblade.mod.input_engine.server.InputEnginePlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.nio.charset.StandardCharsets;

public class InputEngineAPI {

    /**
     * Sends a cooldown for a specific action to the client, which will be displayed
     * on the HUD.
     *
     * @param player     The player to send the cooldown to.
     * @param actionId   The ID of the action (keybind) to put on cooldown.
     * @param durationMs The duration of the cooldown in milliseconds.
     */
    public static void sendCooldown(Player player, String actionId, int durationMs) {
        InputEnginePlugin plugin = JavaPlugin.getPlugin(InputEnginePlugin.class);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        byte[] idBytes = actionId.getBytes(StandardCharsets.UTF_8);
        out.writeInt(idBytes.length);
        out.write(idBytes);
        out.writeInt(durationMs);

        player.sendPluginMessage(plugin, NetworkConstants.FULL_COOLDOWN_CHANNEL, out.toByteArray());
    }

    /**
     * Blocks or unblocks a specific action from being triggered by the client.
     *
     * @param player    The player to block/unblock the input for.
     * @param actionId  The ID of the action (keybind).
     * @param isBlocked Whether the action should be blocked.
     */
    public static void blockInput(Player player, String actionId, boolean isBlocked) {
        InputEnginePlugin plugin = JavaPlugin.getPlugin(InputEnginePlugin.class);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        byte[] idBytes = actionId.getBytes(StandardCharsets.UTF_8);
        out.writeInt(idBytes.length);
        out.write(idBytes);
        out.writeBoolean(isBlocked);

        player.sendPluginMessage(plugin, NetworkConstants.FULL_BLOCK_INPUT_CHANNEL, out.toByteArray());
    }
}
