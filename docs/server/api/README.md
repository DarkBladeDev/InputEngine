# InputEngine Server API

This documentation details how to use the InputEngine API within your own Spigot plugins to register custom keybinds and listen for player inputs.

## Importing the API in your project

First, you need to import the server api from their latest JitPack release.

[![](https://jitpack.io/v/DarkBladeDev/InputEngine.svg)](https://jitpack.io/#DarkBladeDev/InputEngine)

From gradle
```gradle.properties
    repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}

	dependencies {
	        implementation 'com.github.DarkBladeDev:InputEngine:main-SNAPSHOT'
	}
```

From maven
```pom.xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.DarkBladeDev</groupId>
            <artifactId>InputEngine</artifactId>
            <version>main-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

## Getting the Plugin Instance

Now, you need to obtain the instance of the InputEngine plugin to register your keys:

```java
import dev.darkblade.mod.input_engine.server.InputEnginePlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        InputEnginePlugin inputPlugin = (InputEnginePlugin) getServer().getPluginManager().getPlugin("InputEngine");
        
        if (inputPlugin != null) {
            // InputEngine is available
        }
    }
}
```

## Registering Expected Keys

To tell the client mods to listen for specific keys, you must register them. This should ideally be done during your plugin's `onEnable` phase. 

You can register a key with or without translations.

### Basic Registration

```java
// Register a keybind with ID "myplugin:jump", default key 32 (Space), and a translation key
inputPlugin.registerExpectedKey("myplugin:jump", 32, "key.myplugin.jump");
```

### Registration with Translations

You can provide a map of translations so the keybind appears correctly translated on the client side without needing a resource pack:

```java
import java.util.Map;

inputPlugin.registerExpectedKey("example:dash", 86, "key.example.dash", Map.of(
    "en_us", "Dash",
    "es_es", "Embestida",
    "es_mx", "Embestida"
)); // 86 is the GLFW key code for 'V'
```

**Parameters:**
- `actionId` (`String`): A unique identifier for your action (e.g., `namespace:action`).
- `defaultKeyCode` (`int`): The default GLFW key code for the keybind (e.g., 86 for 'V'). (see [GLFW Keycodes documentation](https://www.glfw.org/docs/latest/group__keys.html))
- `translationKey` (`String`): The translation key used in the client's language files or provided translations.
- `translations` (`Map<String, String>`): (Optional) A map of locale codes (like `en_us`) to translated strings.

## Listening to Key Events

When a player presses or releases a registered key, the InputEngine plugin fires a `PlayerKeyPressEvent`.

You can listen to this event like any other Bukkit event:

```java
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KeyInputListener implements Listener {

    @EventHandler
    public void onPlayerKeyPress(PlayerKeyPressEvent event) {
        Player player = event.getPlayer();
        String actionId = event.getActionId();
        boolean isPressed = event.isPressed();

        if (actionId.equals("example:dash")) {
            if (isPressed) {
                player.sendMessage("You pressed the dash key!");
                // Execute dash logic here
            } else {
                player.sendMessage("You released the dash key!");
            }
        }
    }
}
```

**`PlayerKeyPressEvent` Methods:**
- `getPlayer()`: Returns the `Player` who pressed the key.
- `getActionId()`: Returns the `String` ID of the action (matches what you registered).
- `isPressed()`: Returns `true` if the key was just pressed, and `false` if it was released.
