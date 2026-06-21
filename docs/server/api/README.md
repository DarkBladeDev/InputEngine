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

### Advanced Registration

You can specify additional behavior for a keybind, such as requiring modifier keys, double taps, or tracking how long it's held:

```java
// Requires Double Tap, tracks hold duration, and does not require modifiers
inputPlugin.registerExpectedKey(
    "example:dash", 86, "key.example.dash", Map.of(
        "en_us", "Dash",
        "es_es", "Embestida"
    ), false, false, false, true, true, true
);
```

**Parameters:**
- `actionId` (`String`): A unique identifier for your action (e.g., `namespace:action`).
- `defaultKeyCode` (`int`): The default GLFW key code for the keybind (e.g., 86 for 'V'). (see [GLFW Keycodes documentation](https://www.glfw.org/docs/latest/group__keys.html))
- `translationKey` (`String`): The translation key used in the client's language files or provided translations.
- `translations` (`Map<String, String>`): (Optional) A map of locale codes (like `en_us`) to translated strings.
- `hasShift`, `hasCtrl`, `hasAlt` (`boolean`): If `true`, the client will only send the packet if the modifier is held.
- `requiresDoubleTap` (`boolean`): If `true`, requires pressing the key twice within 300ms to trigger.
- `trackHoldDuration` (`boolean`): If `true`, the release event will include the duration (in ms) the key was held.
- `isPartOfCombo` (`boolean`): Informative flag for the client, useful if the key is exclusively used for combos.

## Combos

You can define sequences of keys that trigger a specific `PlayerComboEvent`:

```java
// Register a combo where the player must press "example:attack" followed by "example:dash"
inputPlugin.getComboManager().registerCombo("example:super_dash", java.util.List.of("example:attack", "example:dash"));
```

## Events

For information on how to listen to key presses, releases, double taps, and combos, see the dedicated [Events API Documentation](events/README.md).

## Visual Cooldowns API

You can instruct the client to show a visual cooldown progress bar for a specific action on their screen.

```java
import dev.darkblade.mod.input_engine.server.api.InputEngineAPI;

// Send a 2-second cooldown to the player for the "example:dash" keybind
InputEngineAPI.sendCooldown(player, "example:dash", 2000);
```
*Note: The client can move the cooldown HUD around their screen using the `/inputengine hud` command.*

## Input Blocking API

You can temporarily block or unblock the client from sending input for a specific action. This is useful for stun mechanics or cutscenes.

```java
import dev.darkblade.mod.input_engine.server.api.InputEngineAPI;

// Block the dash key
InputEngineAPI.blockInput(player, "example:dash", true);

// Unblock it later
InputEngineAPI.blockInput(player, "example:dash", false);
```

## Mouse Interception

InputEngine natively intercepts the mouse scroll wheel and extra mouse buttons. You can listen to the following default action IDs without needing to register them explicitly:
- `mouse.scroll.up`
- `mouse.scroll.down`
