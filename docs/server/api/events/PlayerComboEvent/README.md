# PlayerComboEvent

If you registered a combo using the `ComboManager`, you can listen to the `PlayerComboEvent`. This event is fired when a player successfully completes a registered sequence of keys within the allowed time frame.

```java
import dev.darkblade.mod.input_engine.server.api.PlayerComboEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ComboListener implements Listener {

    @EventHandler
    public void onPlayerCombo(PlayerComboEvent event) {
        if (event.getComboId().equals("example:super_dash")) {
            event.getPlayer().sendMessage("Combo Super Dash Triggered!");
        }
    }
}
```

**`PlayerComboEvent` Methods:**
- `getPlayer()`: Returns the `Player` who performed the combo.
- `getComboId()`: Returns the `String` ID of the combo (matches what you registered in `ComboManager`).
