# Changelog - Fabric Client

## [1.2.0] - Dynamic Language Translations
This update brings fully integrated server-side translations, dynamically injected into the client language system without requiring local resource packs.

### ✨ Features
* **Dynamic Translations**: Key names and category titles defined in the server's YAML are now dynamically injected into the client's current language map.

### 🐛 Bug Fixes
* **Language System Stability**: Ensured backward compatibility with servers passing missing translation properties.


## [1.1.2] - Network Protocol Fix
A critical hotfix for connection stability with vanilla servers.

### 🐛 Bug Fixes
* **Server Communication Crash**: Fixed a crash when sending keystrokes to servers without the InputEngine plugin. The mod now verifies if the server supports the custom payload channel before dispatching it.

## [1.1.1] - Polish Update

### 🐛 Bug Fixes
* **Keybind Conflicts**: The mod now silently ignores custom server keybinds if they physically collide with a mapped vanilla action, ensuring the player's primary gameplay is not interrupted.

## [1.1.0] - Extended Input Update
This major update introduces a complete visual overhaul for cooldowns, alongside powerful new input detection methods and mouse interception.

### ✨ Features
* **Cooldown HUD**: Added a dynamic, on-screen visual representation for cooldowns sent by the server.
* **HUD Configuration Screen**: Players can now freely move their Cooldown HUD by using the `/inputengine hud` command, safely storing the coordinates in a local config file.
* **Advanced Key States**: Implemented double-tap detection and hold duration tracking for keys.
* **Modifier Support**: Added detection for Shift, Ctrl, and Alt modifiers when pressing registered keys.
* **Mouse Interception**: Intercepts native mouse scroll actions and extra buttons, sending them to the server seamlessly.
* **Input Block API**: Support for the new server-sided API to freeze or block inputs from specific keys locally.
* **GSON Integration**: Implemented a lightweight client config system (`inputengine-client.json`) using GSON.

## [1.0.4] - Initial Modrinth Release
This is the first official release of the InputEngine Fabric client mod on Modrinth!

### ✨ Features
* **Keybind Registration**: Added custom keybinds for various in-game actions that can be configured in the Minecraft controls menu.
* **Server Communication**: Added a lightweight network payload system to transmit key press and release states to the server in real-time.
* **Vanilla Friendly**: Safely connects to vanilla servers without the server-side plugin installed. The mod disables networking if the server doesn't support it.
* **Version Support**: Compiled and tested for Minecraft 1.21.x.

### 🔧 Requirements
* Requires **Fabric Loader**.
* Requires **Fabric API**.
