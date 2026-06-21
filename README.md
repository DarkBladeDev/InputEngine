# InputEngine

**InputEngine** is a cross-platform bridge (Fabric & NeoForge Client + Spigot Server) designed to capture custom keystrokes from the Minecraft client and transmit them seamlessly to the server. It allows server-side plugins to react instantly to custom client-side keybinds.

## Features

* **Client-Side Keybinding**: Registers custom keybinds on the client (e.g., Skill 1, Skill 2, Dodge). Supports both **Fabric** and **NeoForge**.
* **Advanced Inputs**: Supports Double-Tap detection, Hold duration tracking, and Keyboard Modifiers (Shift/Ctrl/Alt).
* **Mouse Interception**: Natively intercepts scroll wheel and extra mouse buttons for server-side processing.
* **Combo System**: Define key combinations (e.g., `A` -> `Dash`) that trigger special events on the server.
* **Visual Cooldowns & HUD**: Server can send visual cooldowns, which clients can freely position on their screen using `/inputengine hud`.
* **Input Blocking API**: Server can temporarily disable specific keybinds for a player (useful for cutscenes or stuns).
* **Network Bridge**: Sends efficient, lightweight payloads to the server whenever a bound key is pressed or released.
* **Vanilla Server Compatible**: The networking channel is configured as optional on the client. You can safely keep the mod installed while joining vanilla servers.
* **Server-Side API**: Exposes a custom Spigot Event (`PlayerKeyPressEvent`) so your server-side plugins can easily listen and react to player inputs.
* **Modular Structure**: Clean separation between Client, Server, and Common code.

## Project Structure

[![CodeFactor](https://www.codefactor.io/repository/github/darkbladedev/inputengine/badge)](https://www.codefactor.io/repository/github/darkbladedev/inputengine)

This repository is split into several subprojects:

* **`client-fabric`**: The Fabric mod that runs on the player's client.
* **`client-neoforge`**: The NeoForge mod that runs on the player's client.
* **`plugin-spigot`**: The Bukkit/Spigot plugin that runs on the server. It listens to the plugin messaging channel and fires a Bukkit `PlayerKeyPressEvent`.
* **`common`**: Shared constants, payloads, and enums used by both the clients and the server.
* **`example-plugin`**: A lightweight Spigot plugin demonstrating how to listen to the `PlayerKeyPressEvent`.

*(Note: `client-forge` could not be ported to modern versions due to Gradle compatibility issues, so we have fully migrated to `client-neoforge` instead).*

## Installation

### For Players (Client)
1. Download the `client-fabric` or `client-neoforge` jar, depending on your mod loader.
2. Place it in your Minecraft client's `mods/` folder.
3. If using Fabric, ensure you have the [Fabric API](https://modrinth.com/mod/fabric-api) installed.

### For Server Owners (Server)
1. Download the [plugin-spigot](https://modrinth.com/plugin/input-engine/versions?l=spigot) jar.
2. Place it in your Spigot/Paper server's `plugins/` folder.
3. Restart your server.

## For Developers (API Usage)

If you are developing a Spigot plugin and want to react to a player's key press, simply add `plugin-spigot` as a dependency and listen to the `PlayerKeyPressEvent`:

```java
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyPluginListener implements Listener {

    @EventHandler
    public void onPlayerKeyPress(PlayerKeyPressEvent event) {
        if (event.isPressed()) {
            event.getPlayer().sendMessage("You pressed: " + event.getAction().name());
        } else {
            event.getPlayer().sendMessage("You released: " + event.getAction().name());
        }
    }
}
```

Check the `example-plugin` module for a complete, working example.

## License

This project is available under the [MIT License](LICENSE).

## Stats

[![bStats](https://bstats.org/signatures/bukkit/InputEngine.svg)](https://bstats.org/plugin/bukkit/InputEngine/32141)
