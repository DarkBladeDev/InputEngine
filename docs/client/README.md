# Client Documentation

This section provides information about the client-side mods for **Fabric** and **NeoForge**.

## Modules

### Fabric
The `client-fabric` module uses the Fabric API to integrate with the game. It listens for registered keybinds and synchronizes them to the server.

### NeoForge
The `client-neoforge` module utilizes the NeoForge modding API to provide the exact same features as the Fabric version. It ensures cross-loader compatibility.

## Installation
Drop the appropriate `.jar` file for your loader (Fabric or NeoForge) into your `mods` folder. The mods are client-side only and do not need to be installed on a dedicated server (the server uses the Spigot plugin instead).

## Functionality
Both client mods are responsible for:
1. **Receiving Keybind Configurations**: When a player joins a server running the InputEngine plugin, the client receives a list of expected keys to listen for.
2. **Registering Keys**: The client automatically registers these keys into the game's options menu, allowing players to rebind them locally.
3. **Sending Inputs**: Detecting player inputs (presses and releases) for the registered keys and sending them to the server via custom network payloads.

