# InputEngine

**InputEngine** is a cross-platform bridge (Fabric Client & Spigot Server) designed to capture custom keystrokes from the Minecraft client and transmit them seamlessly to the server. It allows server-side plugins to react instantly to custom client-side keybinds.

## Features

* **Client-Side Keybinding**: Registers custom keybinds on the Fabric client (e.g., Skill 1, Skill 2, Dodge).
* **Network Bridge**: Sends efficient, lightweight payloads to the server whenever a bound key is pressed or released.
* **Server-Side API**: Exposes a custom Spigot Event (`PlayerKeyPressEvent`) so your server-side plugins can easily listen and react to player inputs.
* **Modular Structure**: Clean separation between Client, Server, and Common code.

## Project Structure

This repository is split into several subprojects:

* **`client-fabric`**: The Fabric mod that runs on the player's client. It registers the physical keybinds and sends networking payloads when state changes.
* **`server-spigot`**: The Bukkit/Spigot plugin that runs on the server. It listens to the plugin messaging channel and fires a Bukkit `PlayerKeyPressEvent`.
* **`common`**: Shared constants and enums (like `KeyAction` and `NetworkConstants`) used by both the client and the server.
* **`example-plugin`**: A lightweight Spigot plugin demonstrating how to listen to the `PlayerKeyPressEvent`.

## Installation

### For Players (Client)
1. Download the `client-fabric` jar.
2. Place it in your Minecraft client's `mods/` folder.
3. Ensure you have the [Fabric API](https://modrinth.com/mod/fabric-api) installed.

### For Server Owners (Server)
1. Download the `server-spigot` jar.
2. Place it in your Spigot/Paper server's `plugins/` folder.
3. Restart your server.

## For Developers (API Usage)

If you are developing a Spigot plugin and want to react to a player's key press, simply add `server-spigot` as a dependency and listen to the `PlayerKeyPressEvent`:

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
