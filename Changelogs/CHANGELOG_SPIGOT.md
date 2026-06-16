# Changelog - Spigot Plugin

## [1.0.4] - Initial Modrinth Release
This is the first official release of the InputEngine Spigot server plugin on Modrinth!

### ✨ Features
* **Custom Event API**: Added `PlayerKeyPressEvent` which developers can listen to in order to detect player key presses and releases.
* **Network Listener**: Implemented a lightweight plugin messaging channel listener to safely receive and decode key actions from client mods.
* **Seamless Integration**: Fully compatible with Spigot and Paper servers. Players without the client mod will not be affected or disconnected.

### 🛠️ Developer Notes
Check the documentation to see how to easily add the dependency and start listening to player keybinds in your plugins.
