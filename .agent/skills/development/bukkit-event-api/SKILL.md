---
name: bukkit-event-api
description: Guía para crear eventos Bukkit/Spigot correctos que forman parte de la API pública de InputEngine.
trigger: Cuando se crea o modifica un archivo dentro de plugin-spigot/src/.../api/.
---

# Bukkit Event API

## Contexto

El paquete `dev.darkblade.mod.input_engine.server.api` contiene la API pública que los desarrolladores de plugins usan para interactuar con InputEngine. Todo lo que esté en este paquete es un **contrato público** — los cambios deben ser retrocompatibles.

## Estructura de un Evento Bukkit

Seguir este template para crear eventos:

```java
package dev.darkblade.mod.input_engine.server.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when [descripción del evento].
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * @EventHandler
 * public void onEvent(MyEvent event) {
 *     Player player = event.getPlayer();
 *     // handle event
 * }
 * }</pre>
 */
public class MyEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    // ... otros campos finales

    public MyEvent(Player player /*, otros parámetros */) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    // Getters para cada campo — nunca setters

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
```

## Reglas para Eventos API

### Obligatorio
1. **`HandlerList` estático**: Siempre declarar `private static final HandlerList HANDLERS` con `getHandlerList()` estático. Bukkit lo requiere por reflection.
2. **Campos `final`**: Todos los campos del evento deben ser inmutables. Solo getters, nunca setters.
3. **Javadoc completo**: Documentar qué dispara el evento, cuándo se llama, y un ejemplo de uso con `@EventHandler`.
4. **Package `api`**: Todo evento público va en `dev.darkblade.mod.input_engine.server.api`.

### Records como Data Carriers
Para datos complejos asociados a un evento, usar records:

```java
// En el paquete api
public record KeybindData(String actionId, int defaultKey, String translationKey, Map<String, String> translations) {}
```

Los records son ideales porque:
- Son inmutables por diseño
- Generan `equals()`, `hashCode()`, y `toString()` automáticamente
- Son concisos y expresivos

### Eventos Cancelables
Si el evento necesita ser cancelable, implementar `Cancellable`:

```java
public class MyEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }
}
```

## Cómo Disparar un Evento

Desde `InputEnginePlugin` o cualquier clase del servidor:

```java
Bukkit.getScheduler().runTask(this, () -> {
    MyEvent event = new MyEvent(player, data);
    Bukkit.getPluginManager().callEvent(event);
    
    if (!event.isCancelled()) {
        // Procesar resultado
    }
});
```

**Importante**: Siempre disparar eventos en el **main server thread** usando `runTask()`. Los payloads de red llegan en threads de Netty.

## Retrocompatibilidad

La API pública (`server.api`) sigue estas reglas:
- **Nunca eliminar** métodos o clases públicas existentes
- **Nunca cambiar** firmas de métodos existentes
- **Deprecar antes de eliminar**: usar `@Deprecated` con Javadoc explicando la alternativa
- **Agregar es seguro**: nuevos métodos, nuevos campos, nuevos eventos

## Checklist al Crear/Modificar API

- [ ] ¿El evento tiene `HandlerList` estático con `getHandlerList()`?
- [ ] ¿Todos los campos son `final` con solo getters?
- [ ] ¿Hay Javadoc completo con ejemplo de uso?
- [ ] ¿El evento se dispara en el main thread?
- [ ] ¿El cambio es retrocompatible con plugins existentes?
- [ ] ¿Se actualizó el `example-plugin` para demostrar el nuevo evento?
