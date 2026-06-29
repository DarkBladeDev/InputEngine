package dev.darkblade.mod.input_engine.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.darkblade.mod.input_engine.common.NetworkConstants;
import dev.darkblade.mod.input_engine.server.api.KeybindData;
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;

import dev.darkblade.mod.input_engine.server.utils.KeyMapper;
import dev.darkblade.mod.input_engine.server.yaml.YamlKeyManager;
import dev.darkblade.mod.input_engine.server.yaml.action.ActionRegistry;
import dev.darkblade.mod.input_engine.server.yaml.condition.ConditionRegistry;
import dev.darkblade.mod.input_engine.server.yaml.listener.YamlKeyExecutorListener;
import dev.darkblade.mod.input_engine.server.command.InputEngineCommand;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputEnginePlugin extends JavaPlugin implements PluginMessageListener, Listener {

    private final List<KeybindData> registeredKeys = new ArrayList<>();
    private ComboManager comboManager;
    private YamlKeyManager yamlKeyManager;

    @Override
    public void onEnable() {
        getLogger().info("InputEngine Server Plugin is enabling...");

        saveDefaultConfig();

        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                NetworkConstants.FULL_CHANNEL,
                this);

        getServer().getMessenger().registerOutgoingPluginChannel(
                this,
                NetworkConstants.FULL_CONFIG_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(
                this,
                NetworkConstants.FULL_COOLDOWN_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(
                this,
                NetworkConstants.FULL_BLOCK_INPUT_CHANNEL);

        comboManager = new ComboManager();
        getServer().getPluginManager().registerEvents(comboManager, this);
        getServer().getPluginManager().registerEvents(this, this);

        // YAML Keybind System
        ActionRegistry.registerDefaults();
        ConditionRegistry.registerDefaults();
        yamlKeyManager = new YamlKeyManager(this);
        yamlKeyManager.loadKeys();
        getServer().getPluginManager().registerEvents(new YamlKeyExecutorListener(yamlKeyManager), this);

        // Command Registration
        getCommand("inputengine").setExecutor(new InputEngineCommand(this, yamlKeyManager));

        // bStats Metrics
        int pluginId = 32141;
        Metrics metrics = new Metrics(this, pluginId);

        // add custom chart
        // 1. Funciones Avanzadas Utilizadas (AdvancedPie)
        metrics.addCustomChart(new AdvancedPie("advanced_features_usage", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            for (KeybindData key : registeredKeys) {
                if (key.hasShift())
                    valueMap.put("Shift Modifiers", valueMap.getOrDefault("Shift Modifiers", 0) + 1);
                if (key.hasCtrl() || key.hasAlt())
                    valueMap.put("Ctrl/Alt Modifiers", valueMap.getOrDefault("Ctrl/Alt Modifiers", 0) + 1);
                if (key.requiresDoubleTap())
                    valueMap.put("Double Tap", valueMap.getOrDefault("Double Tap", 0) + 1);
                if (key.trackHoldDuration())
                    valueMap.put("Hold Duration", valueMap.getOrDefault("Hold Duration", 0) + 1);
                if (key.isPartOfCombo())
                    valueMap.put("Combos", valueMap.getOrDefault("Combos", 0) + 1);
            }
            if (valueMap.isEmpty()) {
                valueMap.put("None", 1);
            }
            return valueMap;
        }));

        // 2. Teclas por Defecto Más Populares (AdvancedPie)
        metrics.addCustomChart(new AdvancedPie("popular_default_keys", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            for (KeybindData key : registeredKeys) {
                String keyName = KeyMapper.getReadableName(key.defaultKey());
                valueMap.put(keyName, valueMap.getOrDefault(keyName, 0) + 1);
            }
            if (valueMap.isEmpty()) {
                valueMap.put("None", 1);
            }
            return valueMap;
        }));

        // 3. Volumen de Keybinds Registradas (SimplePie)
        metrics.addCustomChart(new SimplePie("registered_keybinds_volume", () -> {
            int size = registeredKeys.size();
            if (size == 0)
                return "0 keys";
            if (size <= 3)
                return "1-3 keys";
            if (size <= 10)
                return "4-10 keys";
            return "10+ keys";
        }));

        // 4. Uso del Combo Manager (SimplePie)
        metrics.addCustomChart(new SimplePie("combo_manager_usage", () -> {
            boolean usesCombos = comboManager.hasRegisteredCombos();
            if (!usesCombos) {
                for (KeybindData key : registeredKeys) {
                    if (key.isPartOfCombo()) {
                        usesCombos = true;
                        break;
                    }
                }
            }
            return usesCombos ? "Yes" : "No";
        }));

        getLogger().info("Registered messaging channel: " + NetworkConstants.FULL_CHANNEL);
        getLogger().info("Registered config channel: " + NetworkConstants.FULL_CONFIG_CHANNEL);
        getLogger().info("Registered cooldown channel: " + NetworkConstants.FULL_COOLDOWN_CHANNEL);
        getLogger().info("Registered block input channel: " + NetworkConstants.FULL_BLOCK_INPUT_CHANNEL);
        getLogger().info("Plugin linked to bStats");
        getLogger().info("InputEngine plugin enabled successfully!");
    }

    public ComboManager getComboManager() {
        return comboManager;
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void registerExpectedKey(String actionId, int defaultKeyCode, String translationKey) {
        registerExpectedKey(actionId, defaultKeyCode, translationKey, java.util.Map.of(), false, false, false, false,
                false, false);
    }

    public void registerExpectedKey(String actionId, int defaultKeyCode, String translationKey,
            java.util.Map<String, java.util.Map<String, String>> translations) {
        registerExpectedKey(actionId, defaultKeyCode, translationKey, translations, false, false, false, false, false,
                false);
    }

    public void registerExpectedKey(String actionId, int defaultKeyCode, String translationKey,
            java.util.Map<String, java.util.Map<String, String>> translations, boolean hasShift, boolean hasCtrl,
            boolean hasAlt,
            boolean requiresDoubleTap, boolean trackHoldDuration, boolean isPartOfCombo) {

        java.util.Map<String, java.util.Map<String, String>> safeTranslations = new java.util.LinkedHashMap<>();
        if (translations != null && !translations.isEmpty()) {
            for (java.util.Map.Entry<?, ?> entry : ((java.util.Map<?, ?>) translations).entrySet()) {
                String key = String.valueOf(entry.getKey());
                Object valObj = entry.getValue();
                if (valObj instanceof java.util.Map<?, ?> mapVal) {
                    java.util.Map<String, String> langMap = new java.util.LinkedHashMap<>();
                    for (java.util.Map.Entry<?, ?> langEntry : mapVal.entrySet()) {
                        langMap.put(String.valueOf(langEntry.getKey()), String.valueOf(langEntry.getValue()));
                    }
                    safeTranslations.put(key, langMap);
                } else if (valObj instanceof String strVal) {
                    safeTranslations.computeIfAbsent(translationKey, k -> new java.util.LinkedHashMap<>()).put(key,
                            strVal);
                }
            }
        }

        registeredKeys
                .add(new KeybindData(actionId, defaultKeyCode, translationKey, safeTranslations, hasShift, hasCtrl,
                        hasAlt, requiresDoubleTap, trackHoldDuration, isPartOfCombo));
    }

    public void clearRegisteredKeys() {
        registeredKeys.clear();
    }

    public void syncToOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendConfigPayload(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (registeredKeys.isEmpty())
            return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            sendConfigPayload(event.getPlayer());
        }, 20L); // Delay to ensure client is ready
    }

    private void sendConfigPayload(Player player) {
        if (registeredKeys.isEmpty())
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(registeredKeys.size());

        for (KeybindData data : registeredKeys) {
            byte[] idBytes = data.actionId().getBytes(StandardCharsets.UTF_8);
            out.writeInt(idBytes.length);
            out.write(idBytes);

            out.writeInt(data.defaultKey());

            byte[] transBytes = data.translationKey().getBytes(StandardCharsets.UTF_8);
            out.writeInt(transBytes.length);
            out.write(transBytes);

            out.writeInt(data.translations().size());
            for (java.util.Map.Entry<String, java.util.Map<String, String>> entry : data.translations().entrySet()) {
                byte[] kBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                out.writeInt(kBytes.length);
                out.write(kBytes);

                java.util.Map<String, String> langMap = entry.getValue();
                out.writeInt(langMap.size());
                for (java.util.Map.Entry<String, String> langEntry : langMap.entrySet()) {
                    byte[] lBytes = langEntry.getKey().getBytes(StandardCharsets.UTF_8);
                    out.writeInt(lBytes.length);
                    out.write(lBytes);

                    byte[] tBytes = langEntry.getValue().getBytes(StandardCharsets.UTF_8);
                    out.writeInt(tBytes.length);
                    out.write(tBytes);
                }
            }

            out.writeBoolean(data.hasShift());
            out.writeBoolean(data.hasCtrl());
            out.writeBoolean(data.hasAlt());
            out.writeBoolean(data.requiresDoubleTap());
            out.writeBoolean(data.trackHoldDuration());
            out.writeBoolean(data.isPartOfCombo());
        }

        player.sendPluginMessage(this, NetworkConstants.FULL_CONFIG_CHANNEL, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(NetworkConstants.FULL_CHANNEL)) {
            return;
        }

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);

            int idLen = in.readInt();
            byte[] idBytes = new byte[idLen];
            in.readFully(idBytes);
            String actionId = new String(idBytes, StandardCharsets.UTF_8);

            boolean isPressed = in.readBoolean();
            long holdDurationMs = in.readLong();
            boolean isDoubleTap = in.readBoolean();

            Bukkit.getScheduler().runTask(this, () -> {
                PlayerKeyPressEvent event = new PlayerKeyPressEvent(player, actionId, isPressed, holdDurationMs,
                        isDoubleTap);
                Bukkit.getPluginManager().callEvent(event);
            });

        } catch (Exception e) {
            getLogger().warning("Failed to decode keystroke payload from " + player.getName() + ": " + e.getMessage());
        }
    }
}
