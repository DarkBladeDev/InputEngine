# Changelog - Spigot Plugin

## [1.4.0] - Translation Engine

### ✨ Features
* **YAML Native Translations**: You can now define translations for multiple languages natively within the `.yml` configurations. You can now define keys like `en_us: "Dash"` nested under a specific `translation_key` or `category` allowing the clients to natively read the translation depending on their current language.

### 🐛 Bug Fixes
* **YAML Path Resolution**: Fixed a major bug where Bukkit's path separation using `.` corrupted translation keys (such as `key.inputengine.test`).
* **API Backward Compatibility**: The plugin API now automatically detects and casts legacy `Map<String, String>` usages from previous versions to safely map them without breaking.


## [1.3.1] - YAML Expansion & Hot-Reloads
This update expands the YAML configuration system with new visual actions, advanced conditions, and real-time reloading capabilities.

### ✨ Features
* **Real-time Reloads**: Added the `/inputengine reload` command. Server administrators can now hot-reload their YAML configurations, and optionally sync them immediately to all online players without requiring a server restart or player reconnection (configurable via `config.yml`).
* **More Actions**: Configured keys can now also play sounds (`sound`), display action bars (`actionbar`), show titles (`title`), and teleport players (`teleport`).
* **More Conditions**: Added new conditions to restrict keybinds based on the player's `world` or `gamemode`.
* **Advanced Cooldowns**: Added a new `cooldown` condition. Cooldowns can share IDs across multiple keys and automatically sync their remaining duration to the client's visual HUD.

## [1.3.0] - YAML Configuration Update
This update transforms InputEngine from a developer API into a standalone utility for server administrators, allowing custom keybinds to be created directly via YAML files.

### ✨ Features
* **YAML Keybinds**: Server admins can now create `.yml` files in the `plugins/InputEngine/keys/` directory to define custom keybinds.
* **Universal Actions**: Configured keys can execute server commands (`console_command`), player commands (`player_command`), or send formatted messages (`message`).
* **Conditional Logic**: Added a condition system (`permission`, `papi`) to restrict when keybinds can be executed.
* **PlaceholderAPI Support**: Fully integrated with PlaceholderAPI to resolve variables like `%player_name%` and `%vault_eco_balance%` inside actions and conditions.

### 🛠️ Developer Notes
* Added the `ActionRegistry` and `ConditionRegistry` APIs. Third-party plugins can now register their own custom action and condition types for server admins to use in their YAML files.


## [1.2.0] - bStats Metrics Update
This update introduces comprehensive bStats integration to track API adoption and feature usage, helping to provide better insights into how InputEngine is used by servers.

### ✨ Features
* **bStats Integration**: Added bStats metrics to the plugin to track total usage across servers.
* **Custom Charts**: Implemented custom charts to visualize Advanced Features Usage, Most Popular Default Keys, Registered Keybinds Volume, and Combo Manager Usage.

### 🛠️ Developer Notes
* Added `dev.darkblade.mod.input_engine.server.utils.KeyMapper` utility class to easily translate raw GLFW key codes into readable string names (e.g., `65` -> `A`, `340` -> `Left Shift`).

## [1.1.0] - Extended Input Update
This major update introduces a complete visual overhaul for cooldowns, alongside powerful new input detection methods, combo support, and mouse interception.

### ✨ Features
* **Combo Manager**: Added `ComboManager` to easily define sequences of inputs (e.g. `A` -> `Dash`) that players can perform to trigger custom actions.
* **Advanced Key States**: `PlayerKeyPressEvent` now contains information about `holdDurationMs`, `isDoubleTap` and modifiers.
* **Visual Cooldowns API**: Added `InputEngineAPI.sendCooldown()` to trigger on-screen cooldown progress bars on the client side.
* **Input Blocking API**: Added `InputEngineAPI.blockInput()` to temporarily lock a player from using a specific keybind.
* **Mouse Interception**: Server can now register vanilla mouse inputs like scroll wheel via the extended client mod.

### 🛠️ Developer Notes
* Added the ability to specify `hasShift`, `hasCtrl`, `hasAlt`, `requiresDoubleTap`, and `trackHoldDuration` when registering an expected keybind.
* Refer to `ExamplePlugin` to see how to implement visual cooldowns and combos.

## [1.0.4] - Initial Modrinth Release
This is the first official release of the InputEngine Spigot server plugin on Modrinth!

### ✨ Features
* **Custom Event API**: Added `PlayerKeyPressEvent` which developers can listen to in order to detect player key presses and releases.
* **Network Listener**: Implemented a lightweight plugin messaging channel listener to safely receive and decode key actions from client mods.
* **Seamless Integration**: Fully compatible with Spigot and Paper servers. Players without the client mod will not be affected or disconnected.

### 🛠️ Developer Notes
Check the documentation to see how to easily add the dependency and start listening to player keybinds in your plugins.
