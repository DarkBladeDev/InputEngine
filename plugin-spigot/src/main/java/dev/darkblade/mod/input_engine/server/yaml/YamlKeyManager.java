package dev.darkblade.mod.input_engine.server.yaml;

import dev.darkblade.mod.input_engine.server.InputEnginePlugin;
import dev.darkblade.mod.input_engine.server.yaml.model.YamlKeybind;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages the loading and storage of YAML-configured keybinds.
 *
 * <p>
 * On startup, this class reads all {@code .yml} files from the
 * {@code plugins/InputEngine/keys/} directory, parses them into
 * {@link YamlKeybind} records, and registers them with the plugin's
 * network system so they are sent to connected clients.
 * </p>
 *
 * <p>
 * If the {@code keys/} directory is empty on first run, an example
 * configuration file is generated automatically.
 * </p>
 */
public class YamlKeyManager {

    private final InputEnginePlugin plugin;
    private final Map<String, YamlKeybind> keybinds = new LinkedHashMap<>();

    /**
     * Creates a new YAML key manager.
     *
     * @param plugin the owning InputEngine plugin instance
     */
    public YamlKeyManager(InputEnginePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads all {@code .yml} files from the {@code keys/} directory,
     * parses them into {@link YamlKeybind} records, and registers each
     * key with the plugin's network system.
     */
    public void loadKeys() {
        File keysDir = new File(plugin.getDataFolder(), "keys");

        if (!keysDir.exists()) {
            keysDir.mkdirs();
        }

        File[] files = keysDir.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null || files.length == 0) {
            generateExampleFile(keysDir);
            plugin.getLogger().info("Generated example keybind file in keys/ directory.");
            return;
        }

        for (File file : files) {
            try {
                loadKeybindFile(file);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING,
                        "Failed to load YAML keybind from " + file.getName() + ": " + e.getMessage(), e);
            }
        }

        plugin.getLogger().info("Loaded " + keybinds.size() + " YAML keybind(s) from keys/ directory.");
    }

    /**
     * Clears all currently loaded keybinds and reloads them from the keys
     * directory.
     */
    public void reloadKeys() {
        keybinds.clear();
        loadKeys();
    }

    /**
     * Returns the {@link YamlKeybind} with the given ID, or {@code null} if not
     * found.
     *
     * @param id the keybind identifier
     * @return the keybind, or {@code null}
     */
    public YamlKeybind getKeybind(String id) {
        return keybinds.get(id);
    }

    /**
     * Returns an unmodifiable collection of all loaded keybinds.
     *
     * @return all loaded {@link YamlKeybind} instances
     */
    public Collection<YamlKeybind> getAllKeybinds() {
        return Collections.unmodifiableCollection(keybinds.values());
    }

    /**
     * Parses a single YAML file into a {@link YamlKeybind} and registers it.
     */
    private void loadKeybindFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String id = yaml.getString("id");
        if (id == null || id.isBlank()) {
            plugin.getLogger().warning("Skipping " + file.getName() + ": missing 'id' field.");
            return;
        }

        if (!yaml.contains("default_key")) {
            plugin.getLogger().warning("Skipping " + file.getName() + ": missing 'default_key' field.");
            return;
        }
        int defaultKey = yaml.getInt("default_key");

        String translationKey = yaml.getString("translation_key", "key.inputengine." + id);

        ConfigurationSection translationStringsSection = yaml.getConfigurationSection("translations");
        Map<String, Map<String, String>> translationStrings = new LinkedHashMap<>();
        if (translationStringsSection != null) {
            for (java.util.Map.Entry<String, Object> entry : translationStringsSection.getValues(false).entrySet()) {
                String transKey = entry.getKey();
                Object val = entry.getValue();
                if (val instanceof ConfigurationSection) {
                    ConfigurationSection langSection = (ConfigurationSection) val;
                    Map<String, String> langMap = new LinkedHashMap<>();
                    for (java.util.Map.Entry<String, Object> langEntry : langSection.getValues(false).entrySet()) {
                        if (langEntry.getValue() instanceof String) {
                            langMap.put(langEntry.getKey(), (String) langEntry.getValue());
                        }
                    }
                    translationStrings.put(transKey, langMap);
                } else if (val instanceof String) {
                    // Fallback to old format: lang -> text (transKey is actually the language)
                    translationStrings.computeIfAbsent(translationKey, k -> new LinkedHashMap<>()).put(transKey, (String) val);
                }
            }
        }

        // Read modifiers
        ConfigurationSection modifiers = yaml.getConfigurationSection("modifiers");
        boolean hasShift = modifiers != null && modifiers.getBoolean("shift", false);
        boolean hasCtrl = modifiers != null && modifiers.getBoolean("ctrl", false);
        boolean hasAlt = modifiers != null && modifiers.getBoolean("alt", false);
        boolean requiresDoubleTap = modifiers != null && modifiers.getBoolean("double_tap", false);
        boolean trackHoldDuration = modifiers != null && modifiers.getBoolean("hold_duration", false);

        // Parse conditions
        List<ConfigurationSection> conditions = parseSectionList(yaml, "conditions");

        // Parse actions
        List<ConfigurationSection> actions = parseSectionList(yaml, "actions");

        YamlKeybind keybind = new YamlKeybind(
                id, defaultKey, translationKey, translationStrings,
                hasShift, hasCtrl, hasAlt,
                requiresDoubleTap, trackHoldDuration,
                conditions, actions);

        keybinds.put(id, keybind);

        // Register with the network system so the key is sent to clients
        plugin.registerExpectedKey(
                id, defaultKey, translationKey, translationStrings,
                hasShift, hasCtrl, hasAlt,
                requiresDoubleTap, trackHoldDuration,
                false);

        plugin.getLogger().info("Loaded YAML keybind: " + id);
    }

    /**
     * Parses a list of maps from the YAML into a list of
     * {@link ConfigurationSection} objects.
     *
     * <p>
     * Each map in the YAML list (e.g. {@code conditions} or {@code actions}) is
     * converted
     * into an in-memory {@link MemoryConfiguration} so that handlers can read
     * values using
     * the familiar {@link ConfigurationSection} API.
     * </p>
     */
    private List<ConfigurationSection> parseSectionList(YamlConfiguration yaml, String key) {
        List<Map<?, ?>> rawList = yaml.getMapList(key);
        if (rawList == null || rawList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ConfigurationSection> sections = new ArrayList<>(rawList.size());
        for (Map<?, ?> map : rawList) {
            MemoryConfiguration section = new MemoryConfiguration();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                section.set(String.valueOf(entry.getKey()), entry.getValue());
            }
            sections.add(section);
        }
        return Collections.unmodifiableList(sections);
    }

    /**
     * Generates an example keybind configuration file in the given directory.
     */
    private void generateExampleFile(File keysDir) {
        File exampleFile = new File(keysDir, "example_key.yml");
        try (FileWriter writer = new FileWriter(exampleFile)) {
            writer.write(
                    """
                            # =============================================================
                            # InputEngine - YAML Keybind Configuration
                            # =============================================================
                            # This is an example keybind configuration file.
                            # Place your .yml files in this directory to create custom keybinds.
                            #
                            # Key codes use GLFW key constants. Common examples:
                            #   R = 82, G = 71, H = 72, V = 86, B = 66
                            #   F1-F12 = 290-301
                            # Full list: https://www.glfw.org/docs/latest/group__keys.html
                            # =============================================================

                            # Unique identifier for this keybind
                            id: "example_shop_key"

                            # GLFW key code for the default key (82 = R)
                            default_key: 82

                            # Translation key used by the client for display
                            translation_key: "key.inputengine.example_shop"

                            translations:
                              "key.inputengine.example_shop":
                                en_us: "Example Shop Key"
                                es_es: "Tecla de Tienda Ejemplo"
                              "category.inputengine.keys":
                                en_us: "InputEngine Keys"
                                es_es: "Teclas de InputEngine"

                            # Modifier keys (all optional, default: false)
                            modifiers:
                              shift: false
                              ctrl: false
                              alt: false
                              double_tap: false
                              hold_duration: false

                            # Conditions that must ALL be met before actions execute (optional)
                            # Supported types: 'permission', 'papi', 'world', 'gamemode', 'cooldown'
                            conditions:
                              - type: 'permission'
                                permission: 'inputengine.example'
                            #  - type: 'world'
                            #    worlds: ['world', 'world_nether']
                            #  - type: 'gamemode'
                            #    gamemodes: ['SURVIVAL']
                            #  - type: 'cooldown'
                            #    id: 'example_ability'
                            #    duration: 5000
                            #    message: '&cPlease wait %time%s before using this again.'
                            #  - type: 'papi'
                            #    placeholder: '%player_health%'
                            #    operator: '>='
                            #    value: '10.0'

                            # Actions to execute when the key is pressed (in order)
                            # Supported types: 'console_command', 'player_command', 'message', 'sound', 'actionbar', 'title', 'teleport'
                            actions:
                              - type: 'message'
                                text: '&aYou pressed the example key!'
                              - type: 'player_command'
                                command: 'help'
                            #  - type: 'sound'
                            #    sound: 'ENTITY_PLAYER_LEVELUP'
                            #  - type: 'actionbar'
                            #    text: '&eKey pressed!'
                            #  - type: 'title'
                            #    title: '&6Warning'
                            #    subtitle: '&7You pressed the key.'
                            #  - type: 'teleport'
                            #    world: 'world'
                            #    x: 0.0
                            #    y: 64.0
                            #    z: 0.0
                            #  - type: 'console_command'
                            #    command: 'say %player_name% used InputEngine!'
                            """);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to generate example keybind file", e);
        }
    }
}
