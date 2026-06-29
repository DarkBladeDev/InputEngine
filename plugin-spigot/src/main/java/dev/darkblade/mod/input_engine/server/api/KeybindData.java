package dev.darkblade.mod.input_engine.server.api;

import java.util.Map;

public record KeybindData(String actionId, int defaultKey, String translationKey, Map<String, Map<String, String>> translations,
        boolean hasShift, boolean hasCtrl, boolean hasAlt, boolean requiresDoubleTap, boolean trackHoldDuration,
        boolean isPartOfCombo) {
    public KeybindData(String actionId, int defaultKey, String translationKey, Map<String, Map<String, String>> translations) {
        this(actionId, defaultKey, translationKey, translations, false, false, false, false, false, false);
    }
}
