package dev.darkblade.mod.input_engine.server.yaml.condition;

import dev.darkblade.mod.input_engine.server.yaml.condition.impl.PermissionCondition;
import dev.darkblade.mod.input_engine.server.yaml.condition.impl.PlaceholderCondition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Central registry for {@link ConditionHandler} implementations used by YAML-configured keybinds.
 *
 * <p>Each condition is identified by a unique string ID (e.g. {@code "permission"}, {@code "papi"}).
 * The registry ships with built-in conditions that are registered via {@link #registerDefaults()},
 * and third-party plugins may register additional conditions at any time.</p>
 *
 * <p>Example registration:</p>
 * <pre>{@code
 * ConditionRegistry.register("in-world", (player, config) -> {
 *     String world = config.getString("world", "world");
 *     return player.getWorld().getName().equals(world);
 * });
 * }</pre>
 *
 * @see ConditionHandler
 */
public final class ConditionRegistry {

    private static final Map<String, ConditionHandler> conditions = new HashMap<>();

    private ConditionRegistry() {
        // Utility class — no instantiation
    }

    /**
     * Registers a condition handler under the given identifier.
     *
     * @param id      the unique condition identifier (case-sensitive)
     * @param handler the handler that evaluates the condition
     * @throws IllegalArgumentException if a condition with the given {@code id} is already registered
     */
    public static void register(String id, ConditionHandler handler) {
        if (conditions.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Condition '" + id + "' is already registered");
        }
        conditions.put(id, handler);
    }

    /**
     * Returns the {@link ConditionHandler} registered under the given identifier,
     * or {@code null} if no handler is registered with that ID.
     *
     * @param id the condition identifier to look up
     * @return the registered handler, or {@code null}
     */
    public static ConditionHandler get(String id) {
        return conditions.get(id);
    }

    /**
     * Registers the built-in default conditions shipped with InputEngine.
     *
     * <p>Currently registered defaults:</p>
     * <ul>
     *   <li>{@code permission} — checks if the player has a specific permission node</li>
     *   <li>{@code papi} — evaluates a PlaceholderAPI placeholder against an expected value</li>
     * </ul>
     */
    public static void registerDefaults() {
        register("permission", new PermissionCondition());
        register("papi", new PlaceholderCondition());
    }

    /**
     * Returns an unmodifiable view of all currently registered condition identifiers.
     *
     * @return an unmodifiable set of registered condition IDs
     */
    public static Set<String> getRegisteredIds() {
        return Collections.unmodifiableSet(conditions.keySet());
    }
}
