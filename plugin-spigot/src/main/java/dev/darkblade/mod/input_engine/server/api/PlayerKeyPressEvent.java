package dev.darkblade.mod.input_engine.server.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKeyPressEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String actionId;
    private final boolean isPressed;
    private final long holdDurationMs;
    private final boolean isDoubleTap;

    public PlayerKeyPressEvent(Player player, String actionId, boolean isPressed) {
        this(player, actionId, isPressed, 0, false);
    }

    public PlayerKeyPressEvent(Player player, String actionId, boolean isPressed, long holdDurationMs, boolean isDoubleTap) {
        this.player = player;
        this.actionId = actionId;
        this.isPressed = isPressed;
        this.holdDurationMs = holdDurationMs;
        this.isDoubleTap = isDoubleTap;
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

    public long getHoldDurationMs() {
        return holdDurationMs;
    }

    public boolean isDoubleTap() {
        return isDoubleTap;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
