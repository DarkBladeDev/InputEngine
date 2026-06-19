# InputEngine Documentation

Welcome to the InputEngine documentation. 

This project consists of client-side mods and a server-side plugin designed to seamlessly synchronize player inputs (key presses and releases) across platforms.

## Structure

- **[Client Documentation](client/README.md)**: Details on the Fabric and NeoForge client mods, how they register keys and send network payloads.
- **[Server Documentation](server/README.md)**: Details on the Spigot server plugin, how it handles packets, and how to use its developer API.
- **[Server API Reference](server/api/README.md)**: Guide for developers to consume the InputEngine API in their own Spigot plugins.

## Getting Started

1. Install the Server Plugin on your Spigot/Paper server.
2. Install the Client Mod on your client (Fabric or NeoForge).
3. Developers can use the [Server API](server/api/README.md) to start registering and listening to custom keybinds.
