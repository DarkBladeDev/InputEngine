package dev.darkblade.mod.input_engine.common;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CooldownManager {
    public static final CooldownManager INSTANCE = new CooldownManager();
    
    // actionId -> CooldownData
    private final Map<String, CooldownData> activeCooldowns = new LinkedHashMap<>();

    public void addCooldown(String actionId, int durationMs) {
        activeCooldowns.put(actionId, new CooldownData(System.currentTimeMillis(), durationMs));
    }

    public void tick() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, CooldownData>> it = activeCooldowns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CooldownData> entry = it.next();
            if (now - entry.getValue().startTime > entry.getValue().durationMs) {
                it.remove();
            }
        }
    }

    public Map<String, CooldownData> getActiveCooldowns() {
        return activeCooldowns;
    }

    public static class CooldownData {
        public long startTime;
        public int durationMs;

        public CooldownData(long startTime, int durationMs) {
            this.startTime = startTime;
            this.durationMs = durationMs;
        }
    }
}
