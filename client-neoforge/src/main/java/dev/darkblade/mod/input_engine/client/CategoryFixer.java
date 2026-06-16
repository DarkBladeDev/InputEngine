package dev.darkblade.mod.input_engine.client;

import net.minecraft.client.KeyMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class CategoryFixer {
    public static void fix() {
        try {
            for (Field field : KeyMapping.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Map.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    Map<?, ?> rawMap = (Map<?, ?>) field.get(null);
                    if (rawMap != null && !rawMap.isEmpty()) {
                        Object firstValue = rawMap.values().iterator().next();
                        if (firstValue instanceof Integer) {
                            @SuppressWarnings("unchecked")
                            Map<String, Integer> map = (Map<String, Integer>) rawMap;
                            if (!map.containsKey("category.inputengine.keys")) {
                                int max = 0;
                                for (Integer val : map.values()) {
                                    if (val != null && val > max) max = val;
                                }
                                map.put("category.inputengine.keys", max + 1);
                                dev.darkblade.mod.input_engine.client.InputEngineNeoForge.LOGGER.info("Injected custom category into KeyMapping map via reflection");
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
