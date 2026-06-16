package dev.darkblade.mod.input_engine.server.api;

import java.util.Map;

public record KeybindData(String actionId, int defaultKey, String translationKey, Map<String, String> translations) {
}
