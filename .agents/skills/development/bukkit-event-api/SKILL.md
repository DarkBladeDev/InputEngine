---
name: bukkit-event-api
description: Guide for creating proper Bukkit/Spigot events that are part of the public InputEngine API.
trigger: When a file is created or modified inside plugin-spigot/src/.../api/.
---

# Bukkit Event API

## Context

The `dev.darkblade.mod.input_engine.server.api` package contains the public API that plugin developers use to interact with InputEngine. Everything in this package is a **public contract** — changes must be backward compatible.

## Bukkit Event Structure

Follow this template to create events:

```java
package dev.darkblade.mod.input_engine.server.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when [event description].
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
    // ... other final fields

    public MyEvent(Player player /*, other parameters */) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    // Getters for each field — never setters

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
```

## Rules for API Events

### Mandatory
1. **Static `HandlerList`**: Always declare `private static final HandlerList HANDLERS` with a static `getHandlerList()`. Bukkit requires this via reflection.
2. **`final` Fields**: All event fields must be immutable. Only getters, never setters.
3. **Complete Javadoc**: Document what triggers the event, when it is called, and a usage example with `@EventHandler`.
4. **`api` Package**: Every public event goes in `dev.darkblade.mod.input_engine.server.api`.

### Records as Data Carriers
For complex data associated with an event, use records:

```java
// In the api package
public record KeybindData(String actionId, int defaultKey, String translationKey, Map<String, String> translations) {}
```

Records are ideal because:
- They are immutable by design
- They generate `equals()`, `hashCode()`, and `toString()` automatically
- They are concise and expressive

### Cancellable Events
If the event needs to be cancellable, implement `Cancellable`:

```java
public class MyEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }
}
```

## How to Fire an Event

From `InputEnginePlugin` or any server class:

```java
Bukkit.getScheduler().runTask(this, () -> {
    MyEvent event = new MyEvent(player, data);
    Bukkit.getPluginManager().callEvent(event);
    
    if (!event.isCancelled()) {
        // Process result
    }
});
```

**Important**: Always fire events on the **main server thread** using `runTask()`. Network payloads arrive on Netty threads.

## Backward Compatibility

The public API (`server.api`) follows these rules:
- **Never remove** existing public methods or classes
- **Never change** existing method signatures
- **Deprecate before removing**: use `@Deprecated` with Javadoc explaining the alternative
- **Adding is safe**: new methods, new fields, new events

## Checklist When Creating/Modifying API

- [ ] Does the event have a static `HandlerList` with `getHandlerList()`?
- [ ] Are all fields `final` with only getters?
- [ ] Is there complete Javadoc with a usage example?
- [ ] Is the event fired on the main thread?
- [ ] Is the change backward compatible with existing plugins?
- [ ] Was the `example-plugin` updated to demonstrate the new event?
