# Changelog - NeoForge Client

## [1.1.3] - Network Protocol Fix
Another critical hotfix for connection stability with servers without the InputEngine plugin installed.

### 🐛 Bug Fixes
* **Server Communication Crash**: Fixed an `UnsupportedOperationException` crash when sending keystrokes to Spigot servers without the InputEngine plugin. The mod now verifies if the server supports the custom payload channel before dispatching it.

## [1.1.2] - Network Protocol Fix
A critical hotfix for connection stability with non-Forge servers.

### 🐛 Bug Fixes
* **Server Communication Crash**: Fixed an `UnsupportedOperationException` crash when sending keystrokes to Spigot servers. The mod now properly bypasses NeoForge's strict network registry checks by wrapping the payload in a vanilla packet.

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
* **Mouse Interception**: Intercepts native mouse scroll actions and extra buttons via NeoForge's `MouseHandler`, sending them to the server seamlessly.
* **Input Block API**: Support for the new server-sided API to freeze or block inputs from specific keys locally.
* **GSON Integration**: Implemented a lightweight client config system (`inputengine-client.json`) using GSON.

## [1.0.4] - Initial Modrinth Release
This is the first official release of the InputEngine NeoForge client mod on Modrinth!

### ✨ Features
* **Keybind Registration**: Added custom keybinds for various in-game actions that can be configured in the Minecraft controls menu.
* **Server Communication**: Added a lightweight network payload system to transmit key press and release states to the server in real-time.
* **Vanilla Friendly**: Safely connects to vanilla servers without the server-side plugin installed. The mod disables networking if the server doesn't support it.
* **Migration**: Fully migrated to NeoForge, providing excellent compatibility for modern Minecraft versions.
* **Version Support**: Compiled and tested for Minecraft 1.21.x.

### 🔧 Requirements
* Requires **NeoForge**.
