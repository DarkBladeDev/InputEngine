package dev.darkblade.mod.input_engine.server;

import dev.darkblade.mod.input_engine.server.api.PlayerComboEvent;
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class ComboManager implements Listener {

    // comboId -> list of actionIds
    private final Map<String, List<String>> registeredCombos = new HashMap<>();

    // player UUID -> list of recent actionIds
    private final Map<UUID, List<ComboEntry>> playerHistory = new HashMap<>();
    private final long COMBO_TIMEOUT_MS = 1000;

    public void registerCombo(String comboId, List<String> actions) {
        registeredCombos.put(comboId, actions);
    }

    @EventHandler
    public void onKeyPress(PlayerKeyPressEvent event) {
        if (!event.isPressed())
            return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        List<ComboEntry> history = playerHistory.computeIfAbsent(uuid, k -> new ArrayList<>());

        // Remove old entries
        history.removeIf(entry -> now - entry.timestamp > COMBO_TIMEOUT_MS);

        // Add new entry
        history.add(new ComboEntry(event.getActionId(), now));

        // Check for combos
        for (Map.Entry<String, List<String>> combo : registeredCombos.entrySet()) {
            List<String> expected = combo.getValue();
            if (history.size() >= expected.size()) {
                // Check if the end of history matches expected
                boolean matches = true;
                int startIdx = history.size() - expected.size();
                for (int i = 0; i < expected.size(); i++) {
                    if (!history.get(startIdx + i).actionId.equals(expected.get(i))) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    // Fire combo event
                    PlayerComboEvent comboEvent = new PlayerComboEvent(player, combo.getKey());
                    Bukkit.getPluginManager().callEvent(comboEvent);

                    // Clear history to prevent rapid repeating
                    history.clear();
                    break;
                }
            }
        }
    }

    private static class ComboEntry {
        String actionId;
        long timestamp;

        public ComboEntry(String actionId, long timestamp) {
            this.actionId = actionId;
            this.timestamp = timestamp;
        }
    }
}
