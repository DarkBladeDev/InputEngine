# Changelog - Spigot Plugin

## [1.3.0] - YAML Configuration Update
This update transforms InputEngine from a developer API into a standalone utility for server administrators, allowing custom keybinds to be created directly via YAML files.

### ✨ Features
* **YAML Keybinds**: Server admins can now create `.yml` files in the `plugins/InputEngine/keys/` directory to define custom keybinds.
* **Universal Actions**: Configured keys can execute server commands (`console_command`), player commands (`player_command`), send formatted messages (`message`), play sounds (`sound`), display action bars (`actionbar`), show titles (`title`), and teleport players (`teleport`).
* **Conditional Logic**: Added a condition system to restrict when keybinds can be executed. Supported conditions: `permission`, `papi`, `world`, `gamemode`, and `cooldown`.
* **Advanced Cooldowns**: Cooldowns (`cooldown`) can now share IDs across multiple keys and automatically sync their remaining duration to the client's visual HUD.
* **Real-time Reloads**: Added the `/inputengine reload` command. Server administrators can now hot-reload their YAML configurations, and optionally sync them immediately to all online players without requiring a server restart or player reconnection.
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
