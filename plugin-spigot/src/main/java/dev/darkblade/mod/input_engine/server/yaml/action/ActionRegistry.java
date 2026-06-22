package dev.darkblade.mod.input_engine.server.yaml.action;

import dev.darkblade.mod.input_engine.server.yaml.action.impl.ConsoleCommandAction;
import dev.darkblade.mod.input_engine.server.yaml.action.impl.MessageAction;
import dev.darkblade.mod.input_engine.server.yaml.action.impl.PlayerCommandAction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for {@link ActionHandler} implementations that can be triggered
 * by YAML-configured keybinds.
 *
 * <p>Built-in action types are registered via {@link #registerDefaults()}:</p>
 * <ul>
 *   <li>{@code console_command} — executes a command as the console</li>
 *   <li>{@code player_command} — executes a command as the player</li>
 *   <li>{@code message} — sends a formatted message to the player</li>
 * </ul>
 *
 * <p>Third-party plugins can register custom action types:</p>
 * <pre>{@code
 * ActionRegistry.register("sound_effect", (player, config) -> {
 *     String sound = config.getString("sound", "entity.experience_orb.pickup");
 *     player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
 * });
 * }</pre>
 *
 * @see ActionHandler
 */
public final class ActionRegistry {

    private static final Map<String, ActionHandler> actions = new HashMap<>();

    private ActionRegistry() {
        // Utility class — no instantiation
    }

    /**
     * Registers an action handler with the given identifier.
     *
     * @param id      the unique action type identifier (e.g. {@code "console_command"})
     * @param handler the handler to execute when this action type is triggered
     * @throws IllegalArgumentException if an action with the given id is already registered
     */
    public static void register(String id, ActionHandler handler) {
        if (actions.containsKey(id)) {
            throw new IllegalArgumentException("Action already registered: " + id);
        }
        actions.put(id, handler);
    }

    /**
     * Returns the action handler registered for the given identifier.
     *
     * @param id the action type identifier
     * @return the registered {@link ActionHandler}, or {@code null} if not found
     */
    public static ActionHandler get(String id) {
        return actions.get(id);
    }

    /**
     * Registers the built-in action types provided by InputEngine.
     *
     * <p>This method should be called once during plugin initialization.
     * It registers the following actions:</p>
     * <ul>
     *   <li>{@code console_command} — {@link ConsoleCommandAction}</li>
     *   <li>{@code player_command} — {@link PlayerCommandAction}</li>
     *   <li>{@code message} — {@link MessageAction}</li>
     * </ul>
     */
    public static void registerDefaults() {
        register("console_command", new ConsoleCommandAction());
        register("player_command", new PlayerCommandAction());
        register("message", new MessageAction());
    }

    /**
     * Returns an unmodifiable set of all registered action type identifiers.
     *
     * @return an unmodifiable {@link Set} of registered action IDs
     */
    public static Set<String> getRegisteredIds() {
        return Collections.unmodifiableSet(actions.keySet());
    }
}
