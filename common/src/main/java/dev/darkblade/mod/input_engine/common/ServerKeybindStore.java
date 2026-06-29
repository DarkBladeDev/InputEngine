package dev.darkblade.mod.input_engine.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ServerKeybindStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File storeFile;

    // Map<ServerIP, Map<ActionID, BoundKeyTranslation>>
    private static Map<String, Map<String, String>> serverKeys = new HashMap<>();

    public static void load(File configDir) {
        storeFile = new File(configDir, "server_keys.json");
        if (storeFile.exists()) {
            try (FileReader reader = new FileReader(storeFile)) {
                Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
                Map<String, Map<String, String>> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    serverKeys = loaded;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        if (storeFile == null) return;
        try (FileWriter writer = new FileWriter(storeFile)) {
            GSON.toJson(serverKeys, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSavedKey(String serverIp, String actionId) {
        if (serverIp == null || serverIp.isEmpty()) {
            serverIp = "singleplayer";
        }
        Map<String, String> serverMap = serverKeys.get(serverIp);
        if (serverMap != null) {
            return serverMap.get(actionId);
        }
        return null;
    }

    public static void saveKey(String serverIp, String actionId, String keyTranslation) {
        if (serverIp == null || serverIp.isEmpty()) {
            serverIp = "singleplayer";
        }
        Map<String, String> serverMap = serverKeys.computeIfAbsent(serverIp, k -> new HashMap<>());
        serverMap.put(actionId, keyTranslation);
    }
}
