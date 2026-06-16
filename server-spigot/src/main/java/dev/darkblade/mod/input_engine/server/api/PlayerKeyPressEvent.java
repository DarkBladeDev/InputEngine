package dev.darkblade.mod.input_engine.server.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKeyPressEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String actionId;
    private final boolean isPressed;

    public PlayerKeyPressEvent(Player player, String actionId, boolean isPressed) {
        this.player = player;
        this.actionId = actionId;
        this.isPressed = isPressed;
    }

    public Player getPlayer() {
        return player;
    }

    public String getActionId() {
        return actionId;
    }

    public boolean isPressed() {
        return isPressed;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
