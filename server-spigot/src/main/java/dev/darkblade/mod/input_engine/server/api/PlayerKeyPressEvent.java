package dev.darkblade.mod.input_engine.server.api;

import dev.darkblade.mod.input_engine.common.KeyAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKeyPressEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final KeyAction action;
    private final boolean isPressed;

    public PlayerKeyPressEvent(Player player, KeyAction action, boolean isPressed) {
        this.player = player;
        this.action = action;
        this.isPressed = isPressed;
    }

    public Player getPlayer() {
        return player;
    }

    public KeyAction getAction() {
        return action;
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
