# Changelog - Spigot Plugin

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
