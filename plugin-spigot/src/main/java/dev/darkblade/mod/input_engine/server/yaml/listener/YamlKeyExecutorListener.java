package dev.darkblade.mod.input_engine.server.yaml.listener;

import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import dev.darkblade.mod.input_engine.server.yaml.YamlKeyManager;
import dev.darkblade.mod.input_engine.server.yaml.action.ActionHandler;
import dev.darkblade.mod.input_engine.server.yaml.action.ActionRegistry;
import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionHandler;
import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionRegistry;
import dev.darkblade.mod.input_engine.server.yaml.model.YamlKeybind;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bukkit event listener that bridges {@link PlayerKeyPressEvent} to YAML-configured
 * keybind actions.
 *
 * <p>When a key press event is received, this listener checks if the action ID
 * corresponds to a YAML-configured keybind. If so, it evaluates all conditions
 * and, if they all pass, executes the action chain in order.</p>
 */
public class YamlKeyExecutorListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger("InputEngine");

    private final YamlKeyManager keyManager;

    /**
     * Creates a new executor listener.
     *
     * @param keyManager the YAML key manager containing loaded keybinds
     */
    public YamlKeyExecutorListener(YamlKeyManager keyManager) {
        this.keyManager = keyManager;
    }

    /**
     * Handles key press events and executes YAML-configured actions.
     *
     * <p>Only processes events where {@link PlayerKeyPressEvent#isPressed()} is {@code true}
     * (key-down events). Key-release events are ignored for action execution.</p>
     *
     * @param event the key press event
     */
    @EventHandler
    public void onKeyPress(PlayerKeyPressEvent event) {
        if (!event.isPressed()) {
            return;
        }

        YamlKeybind keybind = keyManager.getKeybind(event.getActionId());
        if (keybind == null) {
            return; // Not a YAML-configured keybind
        }

        Player player = event.getPlayer();

        // Check all conditions — all must pass
        for (ConfigurationSection conditionConfig : keybind.conditions()) {
            String type = conditionConfig.getString("type");
            if (type == null) {
                LOGGER.warning("[YAML Keys] Condition in keybind '" + keybind.id()
                        + "' is missing 'type' field, skipping condition.");
                continue;
            }

            ConditionHandler handler = ConditionRegistry.get(type);
            if (handler == null) {
                LOGGER.warning("[YAML Keys] Unknown condition type '" + type
                        + "' in keybind '" + keybind.id() + "'.");
                continue;
            }

            if (!handler.check(player, conditionConfig)) {
                return; // Condition not met — do not execute actions
            }
        }

        // Execute all actions in order
        for (ConfigurationSection actionConfig : keybind.actions()) {
            String type = actionConfig.getString("type");
            if (type == null) {
                LOGGER.warning("[YAML Keys] Action in keybind '" + keybind.id()
                        + "' is missing 'type' field, skipping action.");
                continue;
            }

            ActionHandler handler = ActionRegistry.get(type);
            if (handler == null) {
                LOGGER.warning("[YAML Keys] Unknown action type '" + type
                        + "' in keybind '" + keybind.id() + "'.");
                continue;
            }

            try {
                handler.execute(player, actionConfig);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "[YAML Keys] Error executing action '" + type
                        + "' for keybind '" + keybind.id() + "': " + e.getMessage(), e);
            }
        }
    }
}
