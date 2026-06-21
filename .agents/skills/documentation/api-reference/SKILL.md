---
name: api-reference
description: Guide for creating and maintaining API documentation for InputEngine.
trigger: When a public class in the api/ package is added or modified, or when the README.md is updated with API information.
---

# API Reference Documentation

## Public API of InputEngine

The public API is located in `plugin-spigot/src/.../server/api/` and consists of:

| Class | Type | Purpose |
|---|---|---|
| `PlayerKeyPressEvent` | Bukkit Event | Event triggered when a player presses/releases a key |
| `KeybindData` | Record | Data of a registered keybind (actionId, defaultKey, translationKey, translations) |

The `InputEnginePlugin` class also exposes public methods:
| Method | Purpose |
|---|---|
| `registerExpectedKey(actionId, defaultKeyCode, translationKey)` | Registers an expected keybind |
| `registerExpectedKey(actionId, defaultKeyCode, translationKey, translations)` | Registers an expected keybind with translations |

## API Documentation Format

### Javadoc in the Code

Every public class and method must have complete Javadoc:

```java
/**
 * Represents the data for a registered keybind.
 *
 * @param actionId       Unique identifier for the action (e.g., "example:dash")
 * @param defaultKey     Default GLFW key code (e.g., 86 for 'V')
 * @param translationKey Translation key for the keybind name (e.g., "key.example.dash")
 * @param translations   Map of language code to translated name (e.g., {"en_us": "Dash", "es_es": "Embestida"})
 */
public record KeybindData(String actionId, int defaultKey, String translationKey, Map<String, String> translations) {}
```

### README Documentation

The root README must contain a complete, functional API example:

```markdown
## For Developers (API Usage)

If you are developing a Spigot plugin and want to react to a player's key press,
simply add `server-spigot` as a dependency and listen to the `PlayerKeyPressEvent`:

\```java
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
// ... complete example
\```
```

## Rules for Documenting API

### 1. Complete Example
Every documented API element must include:
- **What it does**: Concise one-line description
- **When to use it**: In which context the developer needs it
- **How to use it**: Complete and functional code example
- **Parameters**: Description of each parameter with types and example values

### 2. Action IDs Convention
Document that action IDs follow the format `namespace:action`:
- `example:dash` — Action "dash" from the "example" plugin
- The namespace must be the plugin name in lowercase

### 3. Key Codes
Document that key codes are **GLFW key codes**:
- `86` = V
- `71` = G
- Reference: [GLFW Key Tokens](https://www.glfw.org/docs/latest/group__keys.html)

### 4. Translations
Document the translations format:
```java
Map.of(
    "en_us", "Dash",        // English (mandatory as fallback)
    "es_es", "Embestida",   // Spanish from Spain
    "es_mx", "Embestida"    // Spanish from Mexico
)
```

### 5. Update example-plugin
Whenever the API changes, update `example-plugin/` to reflect the correct usage.
The example-plugin serves as **executable documentation**.

## Template for Documenting a New API Class

```markdown
### `ClassName`

Brief description of what this class represents.

**Package**: `dev.darkblade.mod.input_engine.server.api`

**Usage**:
\```java
// Complete, compilable example
\```

**Methods**:
| Method | Returns | Description |
|---|---|---|
| `getX()` | `Type` | Description |

**Since**: v1.0.X
```

## Checklist When Documenting API

- [ ] Is there complete Javadoc in the Java class/method?
- [ ] Does the README have an updated functional example?
- [ ] Are all parameters documented with types and examples?
- [ ] Was the example-plugin updated?
- [ ] Is the version from which it is available indicated?
