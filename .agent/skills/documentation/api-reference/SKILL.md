---
name: api-reference
description: Guía para crear y mantener documentación de referencia de la API pública de InputEngine.
trigger: Cuando se agrega o modifica una clase pública en el paquete api/, o cuando se actualiza el README.md con información de API.
---

# API Reference Documentation

## API Pública de InputEngine

La API pública se encuentra en `plugin-spigot/src/.../server/api/` y consiste en:

| Clase | Tipo | Propósito |
|---|---|---|
| `PlayerKeyPressEvent` | Bukkit Event | Evento disparado cuando un jugador presiona/suelta una tecla |
| `KeybindData` | Record | Datos de un keybind registrado (actionId, defaultKey, translationKey, translations) |

La clase `InputEnginePlugin` también expone métodos públicos:
| Método | Propósito |
|---|---|
| `registerExpectedKey(actionId, defaultKeyCode, translationKey)` | Registra un keybind esperado |
| `registerExpectedKey(actionId, defaultKeyCode, translationKey, translations)` | Registra un keybind con traducciones |

## Formato de Documentación de API

### Javadoc en el Código

Toda clase y método público debe tener Javadoc completo:

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

### Documentación en README

El README root debe contener un ejemplo funcional y completo de la API:

```markdown
## For Developers (API Usage)

If you are developing a Spigot plugin and want to react to a player's key press,
simply add `server-spigot` as a dependency and listen to the `PlayerKeyPressEvent`:

\```java
import dev.darkblade.mod.input_engine.server.api.PlayerKeyPressEvent;
// ... ejemplo completo
\```
```

## Reglas para Documentar API

### 1. Ejemplo Completo
Todo elemento de API documentado debe incluir:
- **Qué hace**: Descripción concisa de una línea
- **Cuándo usarlo**: En qué contexto el desarrollador lo necesita
- **Cómo usarlo**: Ejemplo de código completo y funcional
- **Parámetros**: Descripción de cada parámetro con tipos y ejemplos de valores

### 2. Convención de Action IDs
Documentar que los action IDs siguen el formato `namespace:action`:
- `example:dash` — Action "dash" del plugin "example"
- El namespace debe ser el nombre del plugin en minúsculas

### 3. Key Codes
Documentar que los key codes son **GLFW key codes**:
- `86` = V
- `71` = G
- Referencia: [GLFW Key Tokens](https://www.glfw.org/docs/latest/group__keys.html)

### 4. Traducciones
Documentar el formato de traducciones:
```java
Map.of(
    "en_us", "Dash",        // Inglés (obligatorio como fallback)
    "es_es", "Embestida",   // Español de España
    "es_mx", "Embestida"    // Español de México
)
```

### 5. Actualizar example-plugin
Cada vez que la API cambie, actualizar `example-plugin/` para reflejar el uso correcto.
El example-plugin sirve como **documentación ejecutable**.

## Template para Documentar una Nueva Clase API

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

## Checklist al Documentar API

- [ ] ¿Hay Javadoc completo en la clase/método Java?
- [ ] ¿El README tiene un ejemplo funcional actualizado?
- [ ] ¿Se documentaron todos los parámetros con tipos y ejemplos?
- [ ] ¿Se actualizó el example-plugin?
- [ ] ¿Se indicó la versión desde la cual está disponible?
