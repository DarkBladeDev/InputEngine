package dev.darkblade.mod.input_engine.server.yaml.condition.impl;

import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Condition that evaluates a PlaceholderAPI placeholder against an expected value.
 *
 * <p>YAML configuration:</p>
 * <pre>{@code
 * - type: 'papi'
 *   placeholder: '%vault_eco_balance%'
 *   operator: '>='
 *   value: '1000'
 * }</pre>
 *
 * <p>Supported operators: {@code ==}, {@code !=}, {@code >}, {@code >=}, {@code <}, {@code <=}.</p>
 *
 * <p>If PlaceholderAPI is not installed, a warning is logged and the condition passes
 * (fail-open) to avoid blocking gameplay.</p>
 */
public class PlaceholderCondition implements ConditionHandler {

    private static final Logger LOGGER = Logger.getLogger("InputEngine");

    @Override
    public boolean check(Player player, ConfigurationSection config) {
        String placeholder = config.getString("placeholder");
        String operator = config.getString("operator", "==");
        String expectedValue = config.getString("value", "");

        if (placeholder == null || placeholder.isBlank()) {
            LOGGER.warning("[YAML Keys] PlaceholderCondition: 'placeholder' is missing or empty, passing by default.");
            return true;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            LOGGER.warning("[YAML Keys] PlaceholderAPI is not installed. "
                    + "Condition with placeholder '" + placeholder + "' will pass by default.");
            return true;
        }

        String resolved = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);

        return evaluate(resolved, operator, expectedValue);
    }

    /**
     * Evaluates the resolved placeholder value against the expected value using the given operator.
     *
     * <p>Attempts numeric comparison first; falls back to string comparison if parsing fails.</p>
     *
     * @param resolved      the resolved placeholder value
     * @param operator      the comparison operator
     * @param expectedValue the expected value to compare against
     * @return {@code true} if the comparison holds, {@code false} otherwise
     */
    private boolean evaluate(String resolved, String operator, String expectedValue) {
        // Try numeric comparison first
        try {
            double resolvedNum = Double.parseDouble(resolved);
            double expectedNum = Double.parseDouble(expectedValue);

            return switch (operator) {
                case "==" -> resolvedNum == expectedNum;
                case "!=" -> resolvedNum != expectedNum;
                case ">" -> resolvedNum > expectedNum;
                case ">=" -> resolvedNum >= expectedNum;
                case "<" -> resolvedNum < expectedNum;
                case "<=" -> resolvedNum <= expectedNum;
                default -> {
                    LOGGER.warning("[YAML Keys] Unknown operator '" + operator + "', defaulting to '=='.");
                    yield resolvedNum == expectedNum;
                }
            };
        } catch (NumberFormatException ignored) {
            // Fall back to string comparison
        }

        // String comparison
        return switch (operator) {
            case "==" -> resolved.equalsIgnoreCase(expectedValue);
            case "!=" -> !resolved.equalsIgnoreCase(expectedValue);
            default -> {
                LOGGER.warning("[YAML Keys] Cannot perform operator '" + operator
                        + "' on non-numeric values: '" + resolved + "' vs '" + expectedValue + "'.");
                yield false;
            }
        };
    }
}
