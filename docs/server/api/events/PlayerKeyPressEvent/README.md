# PlayerKeyPressEvent

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

        if (actionId.equals("example:dash")) {
            if (event.isDoubleTap()) {
                player.sendMessage("You double-tapped the dash key!");
            } else if (!event.isPressed() && event.getHoldDurationMs() > 0) {
                player.sendMessage("You held the dash key for " + event.getHoldDurationMs() + "ms");
            }
        }
    }
}
```

**`PlayerKeyPressEvent` Methods:**
- `getPlayer()`: Returns the `Player` who pressed the key.
- `getActionId()`: Returns the `String` ID of the action (matches what you registered).
- `isPressed()`: Returns `true` if the key was just pressed, and `false` if it was released.
- `isDoubleTap()`: Returns `true` if the action was a double-tap.
- `getHoldDurationMs()`: Returns the time in milliseconds the key was held (requires `trackHoldDuration` to be true when registering the key).
