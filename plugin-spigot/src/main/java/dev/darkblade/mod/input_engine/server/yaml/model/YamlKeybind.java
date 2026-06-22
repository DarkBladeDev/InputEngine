package dev.darkblade.mod.input_engine.server.yaml.model;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Immutable representation of a keybind loaded from a YAML configuration file.
 *
 * <p>Each instance corresponds to a single {@code .yml} file in the
 * {@code plugins/InputEngine/keys/} directory. The {@link #conditions} and
 * {@link #actions} lists store the raw {@link ConfigurationSection} entries
 * from the YAML, allowing handlers to read their own specific parameters.</p>
 *
 * @param id                the unique identifier for this keybind
 * @param defaultKey        the GLFW key code for the default key binding
 * @param translationKey    the translation key used by the client for display
 * @param hasShift          whether the Shift modifier is required
 * @param hasCtrl           whether the Ctrl modifier is required
 * @param hasAlt            whether the Alt modifier is required
 * @param requiresDoubleTap whether a double-tap is required to trigger
 * @param trackHoldDuration whether hold duration should be tracked
 * @param conditions        the list of condition configurations that must all pass
 * @param actions           the list of action configurations to execute in order
 */
public record YamlKeybind(
        String id,
        int defaultKey,
        String translationKey,
        boolean hasShift,
        boolean hasCtrl,
        boolean hasAlt,
        boolean requiresDoubleTap,
        boolean trackHoldDuration,
        List<ConfigurationSection> conditions,
        List<ConfigurationSection> actions
) {
}
